import tempfile
import pytest
from app_server import *


@pytest.fixture
def client():
    db_fd, app.config['DATABASE_FILE_PATH'] = tempfile.mkstemp()
    app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///' + app.config['DATABASE_FILE_PATH']
    app.config['TESTING'] = True
    client = app.test_client()

    with app.app_context():
        db.drop_all()
        db.create_all()

    yield client

    os.close(db_fd)
    os.unlink(app.config['DATABASE_FILE_PATH'])


def test_home_status_code(client):
    r = client.get('/')
    assert r.status_code == 200


def test_register(client):
    r_pk = client.post('user/register', data={"username": "PK", "password": "123"})
    r_pk2 = client.post('user/register', data={"username": "PK", "password": "4545"})
    assert r_pk.status_code == 200
    assert r_pk2.status_code == 404


def test_login_pk(client):
    client.post('user/register', data={"username": "PK", "password": "123"})
    l_pk = client.post('user/login', data={'username': 'PK', 'password': '123'})
    l_ad = client.post('user/login', data={'username': 'AD', 'password': '123'})
    assert l_pk.status_code == 200
    assert l_ad.status_code == 400


def test_logout(client):
    client.post('user/register', data={"username": "GQ", "password": "123"})
    l_gq = client.post('user/login', data={'username': 'GQ', 'password': '123'})
    token = l_gq.json['access_token']
    header_gq = {'Authorization': 'Bearer ' + token}
    lo = client.post('logout', headers=header_gq)
    assert lo.status_code == 200


def test_post(client):
    client.post('user/register', data={"username": "GQ", "password": "123"})
    l1 = client.post('user/login', data={'username': 'GQ', 'password': '123'})
    token = l1.json['access_token']
    header_gq = {'Authorization': 'Bearer ' + token}

    client.post('user/register', data={"username": "PK", "password": "123"})
    l_pk = client.post('user/login', data={'username': 'PK', 'password': '123'})
    token2 = l_pk.json['access_token']
    header_pk = {'Authorization': 'Bearer ' + token2}
    lo = client.post('logout', headers=header_pk)

    p1 = client.post('post', data={'post': "jag lagar egen lunch.",
                                   "image": "aW1nPS9kaXIvZGlyL2hpLXJlcy1pbWcuanBnJnc9NzAwJmg9NTAw", "position": "null"},
                     headers=header_gq)
    p2 = client.post('post', data={'post': "jag lagar egen lunch.",
                                   "image": "aW1nPS9kaXIvZGlyL2hpLXJlcy1pbWcuanBnJnc9NzAwJmg9NTAw",
                                   "position": "svarvaregatan 17"}, headers=header_pk)
    p3 = client.post('post', data={
        'post': "jag lagar egen lunchsdjfi udsfakjfhalsfhsahfkh" +
                "slfkjLFJKSSSSSSSSSSSSSwjiafdsojfsojfosajfsoajSSSheskjdffffffffffffffffffffffffffffffffffffffffffffffffffffsaji" +
                "lfkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkk" +
                "kkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkk" +
                "kkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkfffffffffffffffffffffffffffffffff" +
                "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffSSSSSSSSSSSJsdffffffffsdsfdfsdsfKLSJFs.",
        "image": "aW1nPS9kaXIvZGlyL2hpLXJlcy1pbWcuanBnJnc9NzAwJmg9NTAw", "position": "USA"}, headers=header_gq)
    assert p1.status_code == 200
    assert p2.status_code == 401
    assert p3.status_code == 500


def test_Like_unlike(client):
    client.post('user/register', data={"username": "GQ", "password": "123"})
    l1 = client.post('user/login', data={'username': 'GQ', 'password': '123'})
    token = l1.json['access_token']
    header_gq = {'Authorization': 'Bearer ' + token}

    p1 = client.post('post', data={"post": "jag lagar egen lunch.",
                                   "image": "aW1nPS9kaXIvZGlyL2hpLXJlcy1pbWcuanBnJnc9NzAwJmg9NTAw", "position": "null"},
                     headers=header_gq)
    assert p1.status_code == 200
    id1 = str(p1.json["Posts"][0]["id"])
    liked = client.post('/post/like/or/unlike', data={"postID": id1}, headers=header_gq)
    unliked = client.post('/post/like/or/unlike', data={"postID": id1}, headers=header_gq)
    assert liked.status_code == 200
    assert unliked.status_code == 200


def test_get_number_of_likes(client):
    client.post('user/register', data={"username": "GQ", "password": "123"})
    l1 = client.post('user/login', data={'username': 'GQ', 'password': '123'})
    token = l1.json['access_token']
    header_gq = {'Authorization': 'Bearer ' + token}

    p1 = client.post('post', data={"post": "jag lagar egen lunch.",
                                   "image": "aW1nPS9kaXIvZGlyL2hpLXJlcy1pbWcuanBnJnc9NzAwJmg9NTAw", "position": "null"},
                     headers=header_gq)
    id1 = str(p1.json["Posts"][0]["id"])
    client.post('/post/like/or/unlike', data={"postID": id1}, headers=header_gq)
    answer = client.get('/get_number_of_Likes/' + id1)
    assert answer.json == 1


def test_comment_and_get_comment_for_post(client):
    client.post('user/register', data={"username": "GQ", "password": "123"})
    l1 = client.post('user/login', data={'username': 'GQ', 'password': '123'})
    token = l1.json['access_token']
    header_gq = {'Authorization': 'Bearer ' + token}

    p1 = client.post('post', data={"post": "jag lagar egen lunch.",
                                   "image": "aW1nPS9kaXIvZGlyL2hpLXJlcy1pbWcuanBnJnc9NzAwJmg9NTAw", "position": "null"},
                     headers=header_gq)
    id1 = str(p1.json["Posts"][0]["id"])
    comment = client.post('post/comment', data={"postID": id1, "content": "nice"}, headers=header_gq)
    get_comment = client.get('/get_comment_for_post/' + id1)
    assert comment.status_code == 200
    assert len(get_comment.json["comments"]) == 1


def test_get_post_with_id(client):
    client.post('user/register', data={"username": "GQ", "password": "123"})
    l1 = client.post('user/login', data={'username': 'GQ', 'password': '123'})
    token = l1.json['access_token']
    header_gq = {'Authorization': 'Bearer ' + token}

    p1 = client.post('post', data={"post": "jag lagar egen lunch.",
                                   "image": "aW1nPS9kaXIvZGlyL2hpLXJlcy1pbWcuanBnJnc9NzAwJmg9NTAw", "position": "null"},
                     headers=header_gq)
    id1 = str(p1.json["Posts"][0]["id"])
    get_post = client.get('getpost/' + id1)
    assert len(get_post.json["Posts"]) == 1


def test_get_pic_post_with_id(client):
    client.post('user/register', data={"username": "GQ", "password": "123"})
    l1 = client.post('user/login', data={'username': 'GQ', 'password': '123'})
    token = l1.json['access_token']
    header_gq = {'Authorization': 'Bearer ' + token}

    p1 = client.post('post', data={"post": "jag lagar egen lunch.",
                                   "image": "aW1nPS9kaXIvZGlyL2hpLXJlcy1pbWcuanBnJnc9NzAwJmg9NTAw", "position": "null"},
                     headers=header_gq)
    id1 = str(p1.json["Posts"][0]["id"])
    get_pic_post = client.get('getpicpost/' + id1)
    assert get_pic_post.status_code == 200


def test_get_post_with_id(client):
    client.post('user/register', data={"username": "GQ", "password": "123"})
    l1 = client.post('user/login', data={'username': 'GQ', 'password': '123'})
    token = l1.json['access_token']
    header_gq = {'Authorization': 'Bearer ' + token}

    client.post('post', data={"post": "jag lagar egen lunch.",
                              "image": "aW1nPS9kaXIvZGlyL2hpLXJlcy1pbWcuanBnJnc9NzAwJmg9NTAw", "position": "null"},
                headers=header_gq)

    client.post('post', data={"post": "jag lagar egen lunch.",
                              "image": "aW1nPS9kaXIvZGlyL2hpLXJlcy1pbWcuanBnJnc9NzAwJmg9NTAw", "position": "null"},
                headers=header_gq)

    client.post('post', data={"post": "jag lagar egen lunch.",
                              "image": "aW1nPS9kaXIvZGlyL2hpLXJlcy1pbWcuanBnJnc9NzAwJmg9NTAw", "position": "null"},
                headers=header_gq)

    get_posts = client.get('getallposts')
    assert len(get_posts.json["Posts"]) == 3
