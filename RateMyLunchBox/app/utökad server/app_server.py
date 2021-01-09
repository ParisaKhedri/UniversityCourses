import base64
import datetime
import os
from flask import Flask, request, abort, json, jsonify
from flask_sqlalchemy import SQLAlchemy
from flask_jwt_extended import (
    JWTManager, jwt_required, create_access_token,
    get_jwt_identity,
    get_raw_jwt)
from flask_bcrypt import Bcrypt

if 'NAMESPACE' in os.environ and os.environ['NAMESPACE'] == 'heroku':
    db_uri = os.environ['DATABASE_URL']
    debug_flag = False
else:  # when running locally: use sqlite
    db_path = os.path.join(os.path.dirname(__file__), 'app.db')
    db_uri = 'sqlite:///{}'.format(db_path)
    debug_flag = True

app = Flask(__name__)
app.config['DEBUG'] = debug_flag
app.config['JWT_SECRET_KEY'] = 'super-secret'
app.config['SQLALCHEMY_DATABASE_URI'] = db_uri

app.config['JWT_BLACKLIST_ENABLED'] = True
app.config['JWT_BLACKLIST_TOKEN_CHECKS'] = ["access"]
app.config['JWT_ACCESS_TOKEN_EXPIRATION'] = datetime.timedelta(days=7)
jwt = JWTManager(app)
bcrypt = Bcrypt(app)

print(db_uri)
db = SQLAlchemy(app)

LikedBy = db.Table('LikedBy',
                   db.Column('PostID', db.Integer, db.ForeignKey('Posts.postID'), primary_key=True),
                   db.Column('UserID', db.Integer, db.ForeignKey('User.uID'), primary_key=True))

followers = db.Table('followers',
                     db.Column('followed_by', db.Integer, db.ForeignKey('User.uID'), primary_key=True),
                     db.Column('following', db.Integer, db.ForeignKey('User.uID'), primary_key=True)
                     )


class Posts(db.Model):
    __tablename__ = 'Posts'
    postID = db.Column(db.Integer, primary_key=True, unique=True)
    post = db.Column(db.String(500), unique=False, nullable=False)
    user = db.relationship('User', backref='Posts', lazy=True)
    LikedBy = db.relationship('User', secondary=LikedBy, lazy="subquery", backref=db.backref("liked_posts", lazy=True))
    comments = db.relationship('Comment', backref='Posts', lazy=True)
    image = db.Column(db.LargeBinary, nullable=False)
    position = db.Column(db.String(100), unique=False)

    def to_dict(self):
        likes = []
        for user_liked in self.LikedBy:
            likes.append(user_liked.username)
        comments = []
        for comment in self.comments:
            comments.append(comment.to_dict())
        img = base64.b64encode(self.image).decode('utf-8')
        return {'id': self.postID,
                'post': self.post,
                'likedBy': likes,
                'image': img,
                'comments': comments,
                'position': self.position}


class User(db.Model):
    __tablename__ = 'User'
    uID = db.Column(db.Integer, primary_key=True, unique=True)
    username = db.Column(db.String(140), unique=True, nullable=False)
    password = db.Column(db.String(200), unique=False, nullable=False)
    postID = db.Column(db.Integer, db.ForeignKey('Posts.postID'))
    comments = db.relationship('Comment', backref='User', lazy=True)
    followers = db.relationship('User',
                                secondary=followers, lazy="subquery", backref=db.backref('following', lazy=True))

    def to_dict(self):
        following_list = []
        followers_list = []
        for user in self.following:
            following_list.append(user.username)
        for follower in self.followers:
            followers_list.append(follower.username)
        return {'user_id': self.uID,
                'username': self.username,
                'comments': self.comments,
                'likes': self.liked_post,
                'following': following_list,
                'followers': followers_list}


class Comment(db.Model):
    __tablename__ = 'Comment'
    comment_ID = db.Column(db.Integer, primary_key=True, unique=True)
    content = db.Column(db.String(200), unique=False, nullable=False)
    user = db.Column(db.Integer, db.ForeignKey('User.uID'))
    postID = db.Column(db.Integer, db.ForeignKey('Posts.postID'))

    def to_dict(self):
        user = show_who_commented(self.comment_ID)

        return {"postID": self.postID,
                "content": self.content,
                "user": user}


class Blacklist(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    jti = db.Column(db.String(36), nullable=False)


@jwt.token_in_blacklist_loader
def check_if_token_in_blacklist(decrypted_token):
    jti = decrypted_token["jti"]
    if Blacklist.query.filter_by(jti=jti).first():
        return True
    return False


@app.route('/')
def first_page():
    return "welcome to the first page of RateMyLunchBox"


@app.route('/user/register', methods=["POST"])
def register():
    username = request.form["username"]
    password = request.form["password"]
    user = User.query.filter_by(username=username).first()
    if user is None:
        password_crypt = bcrypt.generate_password_hash(password=password).decode('utf-8')
        user = User(username=username, password=password_crypt)
        db.session.add(user)
        db.session.commit()
        return jsonify({"message": "registered"}), 200
    return jsonify({"message": "username already taken "}), 404


@app.route('/user/login', methods=["POST"])
def login():
    username = request.form["username"]
    password = request.form["password"]
    user = User.query.filter_by(username=username).first()
    if user is None:
        return jsonify({"error": "no such user"}), 400
    if bcrypt.check_password_hash(user.password, password):
        token = create_access_token(identity=user.uID)
        return jsonify(access_token=token), 200
    return jsonify({"error": "wrong password"}), 400


@app.route('/logout', methods=["POST"])
@jwt_required
def logout():
    jti = get_raw_jwt()['jti']
    jti_value = Blacklist(jti=jti)
    db.session.add(jti_value)
    db.session.commit()
    return 'Access token revoked', 200


@app.route('/post', methods=["POST"])
@jwt_required
def post():
    if "post" in request.form:
        print("kommer vi till ifsatsen???")
        post = request.form['post']
        image = base64.b64decode(request.form['image'])
        position = request.form['position']
        if len(post) <= 500:
            ms = Posts(post=post, image=image, position=position)
            db.session.add(ms)
            db.session.commit()
            return jsonify({"Posts": [ms.to_dict()]}), 200
        else:
            abort(500)
    else:
        abort(400)


@app.route('/getpicpost/<postID>', methods=["GET"])
def get_pic_post(postID):
    the_post = Posts.query.filter_by(postID=int(postID)).first()
    if the_post is not None:
        return base64.b64encode(the_post.image).decode('utf-8')
    else:
        abort(400)


@app.route('/getallposts', methods=["GET"])
def get_all_posts():
    allPosts = []
    for post in Posts.query.all():
        if len(Posts.query.all()) != 0:
            allPosts.append(post.to_dict())
    return jsonify({"Posts": allPosts}), 200


@app.route('/getpost/<postID>', methods=["GET"])
def get_post(postID):
    the_post = Posts.query.filter_by(postID=int(postID)).first()
    if the_post is not None:
        return jsonify({"Posts": [the_post.to_dict()]}), 200
    else:
        abort(400)


@app.route('/getpost/<username>', methods=["GET"])
@jwt_required
def get_users_posts(username):
    users_posts = []
    the_user = User.query.filter_by(username=username).first()
    for post in Posts.query.all():
        if post.user == the_user:
            users_posts.append(post.to_dict)
    return jsonify({"posts": users_posts}), 200

# def som kolla på båda users following table om båda users finns med i varandras tabeller då är de vännr
# isatsen måste på nåt sätt ha koll på alla users folling table helatilden hur ska vi
# ha en request som kallar på denn funktionen??
# follow user läggs till i din following lista samt andra anvöndarens follower lista
# kolla om en user finns både på follwer och following lista då är den personen min vän
# requesten kan kallas på då användare kliker på knappen show my firends
# när man follow eller unfollow en user ska gerfirends requesten kalla i bagkgrunden och refresha firendlistan.


@app.route('/getfriends/<username>', methods=["GET"])
@jwt_required
def get_friends(username):
    friends = []
    the_user = User.query.filter_by(username=username).first()
    for user in User.querry.all():
        if user.username in the_user.followers and user.username in the_user.following:
            friends.append(user.username)
    return jsonify({'friends': friends}), 200


@app.route('/get_number_of_Likes/<postID>', methods=["GET"])
def get_number_of_likes(postID):
    the_post = Posts.query.filter_by(postID=int(postID)).first()
    likes = the_post.to_dict()['likedBy']
    return jsonify(len(likes))


@app.route('/get_comment_for_post/<The_postID>', methods=["GET"])
def get_comment_for_post(The_postID):
    the_comments = []
    for comment in Comment.query.all():
        print("comment-postID", comment.postID)
        print("The_postID", The_postID)
        if comment.postID == int(The_postID):
            print("kommer till ifsatsen get comment for post")
            the_comments.append(comment.to_dict())
    return jsonify({"comments": the_comments}), 200


def show_who_commented(commentID):
    comment = Comment.query.filter_by(comment_ID=commentID).first()
    user = User.query.filter_by(uID=comment.user).first()
    return user.username


@app.route('/post/like/or/unlike', methods=["POST"])
@jwt_required
def like_unlike_post():
    postID = int(request.form["postID"])
    the_post = Posts.query.filter_by(postID=postID).first()
    uID = get_jwt_identity()
    print("detta är uID", uID)
    user = User.query.filter_by(uID=uID).first()
    if user is None:
        return "user not found"
    if user in the_post.LikedBy:
        the_post.LikedBy.remove(user)
        db.session.commit()
        return 'Post unliked', 200
    else:
        user.liked_posts.append(the_post)
        db.session.commit()
        return 'Post liked', 200


@app.route('/post/comment', methods=["POST"])
@jwt_required
def post_comment():
    if "content" in request.form:
        the_comment = request.form["content"]
        postID = request.form["postID"]
        uID = get_jwt_identity()
        if len(the_comment) <= 200:
            ms = Comment(postID=postID, user=uID, content=the_comment)
            db.session.add(ms)
            db.session.commit()
            return json.dumps({'success': True}), 200, {'ContentType': 'application/json'}
        else:
            abort(500)
    else:
        abort(400)


@app.route('/get/followers/for/<uID>', methods=["GET"])
@jwt_required
def get_followers(uID):
    user = User.query.filter_by(uID=uID).first()
    followers_list = user.to_dict()["followers"]
    if len(followers_list) != 0:
        return jsonify({'followers': followers_list}), 200
    else:
        return jsonify({'followers': []}), 200


@app.route('/get/following/for/<uID>', methods=["GET"])
@jwt_required
def get_following(uID):
    user = User.query.filter_by(uID=uID).first()
    following_list = user.to_dict()["following"]
    if len(following_list) != 0:
        return jsonify({'followers': following_list}), 200
    else:
        return jsonify({'followers': []}), 200


@app.route('/get/number/of/followings/<uID>', methods=["GET"])
def get_number_of_followings(uID):
    user = User.query.filter_by(uID=uID).first()
    following_list = user.to_dict()["following"]
    return jsonify(len(following_list))


@app.route('/get/number/of/followers/<uID>', methods=["GET"])
def get_number_of_followers(uID):
    user = User.query.filter_by(uID=uID).first()
    followers_list = user.to_dict()["followers"]
    return jsonify(len(followers_list))


@app.route('/follow/user', methods=["Post"])
@jwt_required
def follow_unfollow_user():

    followerID = get_jwt_identity()
    follower_user = User.query.filter_by(uID=followerID).first()
    follower_username = follower_user.username
    userID = int(request.form["uID"])
    user = User.query.filter_by(uID=userID).first()

    if follower_user in user.followers:
        user.followers.remove(follower_user)
        db.session.commit()
        return "user unfollowed", 200
    else:
        user.followers.append(follower_username)
        db.session.commit()
        return "user followed", 200






@app.route('/restart')
def restart():
    db.drop_all()
    db.create_all()
    return ""


if __name__ == '__main__':
    app.run(port=9000)
