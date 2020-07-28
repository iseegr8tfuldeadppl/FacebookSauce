import requests

MUST_SENDING_ACCESS_TOKEN = "EAAJUrqnnDw4BADM2Cb9c1H45xT38dmZCZCBiY1UHB5H96MAybZAufi9YwLPvcH40Wbq2vLsA5AacljDYVndsq036rsvN4XPT1xfwJsrNFooBijeXTBgh5QTYzof8F2AIj47IHHlR7AKjF7ERz3cDmL42oFbGTIG6hEGXHnIc8CytZCVlrp7m"

mustshop_page_id = "100439491416453"
RECEIVING_API_URL = "https://graph.facebook.com/v5.0/" + mustshop_page_id + "/conversations"

# amine_ben_id = "764691244344243"
#
# url = "https://graph.facebook.com/" + amine_ben_id + "/accounts"
#
# auth = {
#     "access_token": "EAA7LTgIYV8oBAElrgPbyE6srpRbd5EY077zZB9821dcYlZBm4otz7y1NXwsmeZC8KO9ZC9wLBC0VR9O8A5wImkQA2hmeCI3IttZBGvNmj7DYxhAcKl3ZAjRogZAKFxZBXF1n1vwMLqebLrjm1NMXP4ySHW151Ban3J5ArZBUemVXiGWq2OjE9u9iayaOihjbmoFoZD"
# }
#
# response = requests.get(url, params=auth)
#
# print(response.content)


receiving_data = {
    "access_token": MUST_SENDING_ACCESS_TOKEN,
    "fields": "participants,link",
    "limit": 499
}

response = requests.get(RECEIVING_API_URL, params=receiving_data)
print(response.content)
