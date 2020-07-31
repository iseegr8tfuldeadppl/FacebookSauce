# phoon app id
app_id = "2505320266356258"
amine_ben_id = "3352531754819787"
mustshop_page_id = "100439491416453"
redditmemesbot_page_id = "437256150456258"

REDDITMEMESBOT_SENDING_ACCESS_TOKEN = "EAAjmk1SGViIBADV4vZBrJ0gnNHfx0uV5cr6voZC8zsZCJh6jqne268J2K8xSodZAMAwkk7ZBuQnlvdbSmgZAeMZA35UKdKVoZCqa6ziw32U3qYL1NGsHfHdZARMmT9mGfw6HkGT4EIrQZA1h3SiyyZBzjk40GU5FSlyJfs94ac2t7H5wZB2Jxoh3DB2N"

MUST_SENDING_ACCESS_TOKEN = "EAAjmk1SGViIBAI2HZCF8huB5Vs6a4bBai93drnLHR0wYhXKH2dhlO1mz75sK5IpWGMZBoJdwZCsM6ioij2PZAHYvfZArQbxZBfM1EO6lBJeLuT8nhkfjNUEzIKZAIO6VCe1lBKC23IO2WOGbG3vanjwAlQTP8Ar4j2lI53iQ8VZAzHGW0fGaEQyQ"

MUST_SENDING_ACCESS_TOKEN = "EAAjmk1SGViIBANUZCGLZBMbzw6bwLvKZC7NhBRNNXrVbZB30oaEwQNvnRmEQxml7OitpUjcpIOjdPpMty29wtj3tHqKZAZClCuTZBXwZCP43uWDtYRvobkdpWZCf2BHGABXeurLAdaZA2NY6RWiZCZAQvXNFRmQxykhFZBYZAwgppV38m8wOZCPxIhpl43kDUrYvbamGesZD"

# Receiving information
# get clients page
receiving_test_url = "https://graph.facebook.com/v7.0/" + mustshop_page_id + "/conversations?fields=participants,link&limit=499&access_token=" + MUST_SENDING_ACCESS_TOKEN

receiving_auth = {'access_token': MUST_SENDING_ACCESS_TOKEN}

receiving_data = {
    "access_token": MUST_SENDING_ACCESS_TOKEN,
    "fields": "participants,link",
    "limit": 499
}

RECEIVING_API_URL = "https://graph.facebook.com/v5.0/" + mustshop_page_id + "/conversations"



# Sending information
SENDING_API_URL = 'https://graph.facebook.com/v7.0/me/messages'# optimal v2.6

sending_auth = {
    "access_token": MUST_SENDING_ACCESS_TOKEN
}

sending_data = {
    "recipient":{
        "id": None
    },
    "message": {
        "text": None
    },
    "messaging_type": "MESSAGE_TAG",
    "tag": "ACCOUNT_UPDATE"
}
