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


class Comment(db.Model):
    __tablename__ = 'Comment'
    comment_ID = db.Column(db.Integer, primary_key=True, unique=True)
    content = db.Column(db.String(200), unique=False, nullable=False)
    user = db.Column(db.Integer, db.ForeignKey('User.uID'))
    postID = db.Column(db.Integer, db.ForeignKey('Posts.postID'))

    def to_dict(self):
        user = show_who_commented(self.comment_ID)

        return{"postID": self.postID,
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
    return"welcome to the first page of RateMyLunchBox"


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
    if "username" in request.form and "password" in request.form:
        username = request.form["username"]
        password = request.form["password"]
        user = User.query.filter_by(username=username).first()
        if user is None:
            return jsonify({"error": "no such user"}), 400
        elif bcrypt.check_password_hash(user.password, password):
            token = create_access_token(identity=user.uID)
            return jsonify(access_token=token), 200
        return jsonify({"error": "wrong password"}), 400
    else:
        return jsonify({"error": "missing data"}), 400


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
        abort(401)


@app.route('/getpost/<postID>', methods=["GET"])
def get_post(postID):
    the_post = Posts.query.filter_by(postID=int(postID)).first()
    if the_post is not None:
        return jsonify({"Posts": [the_post.to_dict()]}), 200
    else:
        abort(400)


@app.route('/getpicpost/<postID>', methods=["GET"])
def get_pic_post(postID):
    the_post = Posts.query.filter_by(postID=int(postID)).first()
    if the_post is not None:
        return base64.b64encode(the_post.image).decode('utf-8'), 200
    else:
        abort(400)


@app.route('/getallposts', methods=["GET"])
def get_all_posts():
    allPosts = []
    for post in Posts.query.all():
        if len(Posts.query.all()) != 0:
            allPosts.append(post.to_dict())
    return jsonify({"Posts": allPosts}), 200


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
    uID= get_jwt_identity()
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


@app.route('/restart')
def restart():
    db.drop_all()
    db.create_all()
    return ""

if __name__ == '__main__':
    app.run(port=9000)
