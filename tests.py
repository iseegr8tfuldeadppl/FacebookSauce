import requests


receiving_data = {
    "access_token": "EAAjmk1SGViIBABfCoUjQZCT6JOpOom1mwi2bLpsz4Lbbgn5V4W9zRXRVS8fHYLbbv9rWmM473F0euFOE5Jn1EMw2pAn4h3ZB5744VD4R6Y7ZCIaWqhihbvZCuSUGWn4MZAdHVQ3OXf8seLmmMLoXMtZAw6ZCiYvReaU7JLDDZBpwK03gXZB5nUyIZA",
    "fields": "participants,link",
    "limit": 499
}

RECEIVING_API_URL = "https://graph.facebook.com/v5.0/" + "100439491416453" + "/conversations"

response = requests.get(RECEIVING_API_URL, params=receiving_data)

print(response.content)
