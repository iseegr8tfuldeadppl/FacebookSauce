from values import sending_auth, mustshop_page_id
import requests

receiving_test_url = "https://graph.facebook.com/" + mustshop_page_id + "/messages"


json = {
    "recipient": {
        "id": "3352531754819787"
    },
    "sender_action":"mark_seen"
}

response = requests.post(receiving_test_url, params=sending_auth, json=json)
print(response.content)
