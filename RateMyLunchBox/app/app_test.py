import requests

if __name__ == '__main__':
    #url = 'http://127.0.0.1:9000/'
    url = 'https://ratemylunchbox.herokuapp.com/'

    setup = requests.get(url + 'restart')
    print("restart status code", setup.status_code)

    register = requests.post(url + 'user/register', data={"username": "GQ", "password": "123"})
    print("register", register.status_code)
    inloggning = requests.post(url + 'user/login', data={'username': 'GQ', 'password': '123'})
    print("inlogning", inloggning.status_code)
    token = inloggning.json()['access_token']
    headerGQ = {'Authorization': 'Bearer ' + token}

    register2 = requests.post(url + 'user/register', data={"username": "PK", "password": "123"})
    print("register2", register2.status_code)
    inloggning2 = requests.post(url + 'user/login', data={'username': 'PK', 'password': '123'})
    print("inlogning2", inloggning2.json())

    # ad användare
    register3 = requests.post(url + 'user/register', data={"username": "AD", "password": "123"})
    print("register3", register3.status_code)
    inloggning3 = requests.post(url + 'user/login', data={'username': 'AD', 'password': '123'})
    print("inlogning3", inloggning3.json())

    """token2 = inloggning2.json()['access_token']
    headerPK = {'Authorization': 'Bearer ' + token2}

    post_message1 = requests.post(url + 'post',
                                  data={'post': "jag lagar egen lunch.",
                                        "image": "aW1nPS9kaXIvZGlyL2hpLXJlcy1pbWcuanBnJnc9NzAwJmg9NTAw",
                                        "position": "null"}, headers=headerGQ)
    print("post message1", post_message1.status_code)
    post_message2 = requests.post(url + 'post', data={'post': "jag ater lunch.",
                                                      "image": "aW1nPS9kaXIvZGlyL2hpLXJlcy1pbWcuanBnJnc9NzAwJmg9NTAw",
                                                      "position": "null"}, headers=headerPK)
    print("post message2", post_message2.status_code)

    ID1 = str(post_message1.json()["Posts"][0]["id"])
    ID2 = str(post_message2.json()["Posts"][0]["id"])

    post_liked = requests.post(url + '/post/like/or/unlike', data={"postID": ID1}, headers=headerPK)
    print("post_liked status", post_liked.status_code)

    post_liked2 = requests.post(url + '/post/like/or/unlike', data={"postID": ID1}, headers=headerGQ)
    print("post_liked status", post_liked2.status_code)

    post_unliked = requests.post(url + '/post/like/or/unlike', data={"postID": ID1}, headers=headerGQ)
    print("post_unliked status", post_unliked.status_code)

    post_liked3 = requests.post(url + '/post/like/or/unlike', data={"postID": ID2}, headers=headerGQ)
    print("post_liked status", post_liked3.status_code)

    comment1 = requests.post(url + 'post/comment', data={"postID": ID2, "content": "nice"}, headers=headerGQ)
    print("post_comment status", comment1.status_code)

    comment2 = requests.post(url + 'post/comment', data={"postID": ID1, "content": "bra jobbat"}, headers=headerGQ)
    print("post_comment2 status", comment2.status_code)

    get_post = requests.get(url + 'getpost/' + str(ID1), headers=headerPK)
    print("get post with id id1", get_post.json())
    get_post2 = requests.get(url + 'getpost/' + str(ID2), headers=headerGQ)
    print("get post with id id2", get_post2.json())
    get_likes_ID1 = requests.get(url + 'get_number_of_Likes/' + str(ID1), headers=headerGQ)
    print("getlikes", get_likes_ID1.json())

    get_all_posts = requests.get(url + 'getallposts')
    print("status code for get all posts", get_all_posts.status_code)
    print("get all post", get_all_posts.json())

    get_comment2 = requests.get(url + 'get_comment_for_post/' + str(ID2), headers=headerGQ)
    print("status koden för get_comment ID2", get_comment2.json())
    get_comment = requests.get(url + 'get_comment_for_post/' + str(ID1), headers=headerGQ)
    print("status koden för gets-comment ID1", get_comment.json())

    logout = requests.post(url + 'logout', headers=headerGQ)
    print("loged out", logout.status_code)"""
