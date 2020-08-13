import requests
amine_ben_id = "3352531754819787"
mustshop_page_id = "100439491416453"
FB_API_URL = 'https://graph.facebook.com/v5.0/'# optimal v2.6
MUST_ACCESS_TOKEN = "EAAjmk1SGViIBAI2HZCF8huB5Vs6a4bBai93drnLHR0wYhXKH2dhlO1mz75sK5IpWGMZBoJdwZCsM6ioij2PZAHYvfZArQbxZBfM1EO6lBJeLuT8nhkfjNUEzIKZAIO6VCe1lBKC23IO2WOGbG3vanjwAlQTP8Ar4j2lI53iQ8VZAzHGW0fGaEQyQ"

RECEIVING_API_URL = "https://graph.facebook.com/v8.0/" + mustshop_page_id + "/conversations"
MUST_RECEIVING_ACCESS_TOKEN = "EAAJUrqnnDw4BADM2Cb9c1H45xT38dmZCZCBiY1UHB5H96MAybZAufi9YwLPvcH40Wbq2vLsA5AacljDYVndsq036rsvN4XPT1xfwJsrNFooBijeXTBgh5QTYzof8F2AIj47IHHlR7AKjF7ERz3cDmL42oFbGTIG6hEGXHnIc8CytZCVlrp7m"

receiving_data = {
    "access_token": MUST_RECEIVING_ACCESS_TOKEN,
    "fields": "participants,link",
    "limit": 1
}
auth = {
    "access_token": MUST_RECEIVING_ACCESS_TOKEN,
        "fields": "message,from,link"
}
accss = {
    "access_token": MUST_RECEIVING_ACCESS_TOKEN,
}


file = open("images_received.txt", "r", encoding="utf-8-sig")
file2 = open("output_with_all_info_4.txt", "r", encoding="utf-8-sig")
lines = file2.read().split("\n")[:-1]
file2.close()
ids_of_clients_who_sent_images = []
for line in file.read().split("\n"):
    ids_of_clients_who_sent_images.append(line)
file.close()

done_ids = []

for id in ids_of_clients_who_sent_images:
    if not done_ids.includes(id) and amine_ben_id!=id:
        done_ids.append(id)
        conversation_id = None
        output = ""
        for line in lines:
            if line.find(id)>-1:
                client = eval(line)
                conversation_id = client["id"]
                output += client["participants"]["data"][0]["name"] + " " + client["id"] + "\n"
                print("checking {}".format(client["participants"]["data"][0]["name"] ))
                break

        if not conversation_id:
            print("{} isn't on the output list please refresh output list".format(id))
        else:
            #message_count = 0
            image_count = 0
            while True:
                response = requests.get(FB_API_URL + conversation_id + "/messages", params=auth)
                content = eval(response.text)
                messages = content["data"]
                for message in messages:
                    #message_count += 1
                    #print("message_count {}".format(message_count))
                    if message["from"]["id"]!=mustshop_page_id:
                        # response = requests.get(FB_API_URL + message["id"] + "/shares", params=auth)
                        # print(response.text)
                        if not message["message"]:
                            response = requests.get(FB_API_URL + message["id"] + "/attachments", params=accss)
                            attachments = eval(response.text.replace("false", "False").replace("true", "True"))
                            if len(attachments["data"])>0:
                                if attachments["data"][0].get("image_data"):
                                    image_count += 1
                                    url = attachments["data"][0]["image_data"]["url"].replace("\\", "")
                                    output += url + "\n"
                if content.get("paging"):
                    auth.update({"after":content["paging"]["cursors"]["after"]})
                else:
                    auth.update({"after":""})
                    break
            if image_count>0:
                print("saved {} with image_count {}".format(output.split("\n")[0], image_count))
                output += "\n\n"
                file3 = open("images.txt", "a", encoding="utf-8-sig")
                file3.write(output)
                file3.close()

# response = requests.get(RECEIVING_API_URL, params=receiving_data)
# print(response.content)
