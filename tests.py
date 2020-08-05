import requests

MUST_SENDING_ACCESS_TOKEN = "EAAJUrqnnDw4BAO7CEz0OuKJn0UgGiPstHY5jMSXZBjDfTtiLvPeK0xLkyTcKT9iuHbn6smg2of56NCrKl6Kzwzn4BnWQrm7aieVYUrheZAjKiUiCHLrRy6k1kmWhNUxqSnkPbZC3NfU1xoLPIxN47aKZAZCIiFO04LTD6AZAE2Dhrj8toU5d0W"

SENDING_API_URL = 'https://graph.facebook.com/v7.0/me/messages'# optimal v2.6

sending_auth = {
    "access_token": MUST_SENDING_ACCESS_TOKEN
}

sending_data = {
    "recipient":{
        "id": "3352531754819787"
    },
    "message": {
        "text": "ahhh"
    },
    "messaging_type": "MESSAGE_TAG",
    "tag": "ACCOUNT_UPDATE"
}

for i in range(0, 3000):
    response = requests.post(SENDING_API_URL, params=sending_auth, json=sending_data)
    print(response.content)
