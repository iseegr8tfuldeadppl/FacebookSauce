import requests

# load sending variables
from values import sending_auth, sending_data, SENDING_API_URL

# load receiving variables
from values import receiving_auth, receiving_data, RECEIVING_API_URL


text_file = "output_with_all_info_2.txt"

def load_page_of_clients(data):

    # Step 1: request 499 clients from facebook
    response = requests.get(RECEIVING_API_URL, params=data)

    # Step 2: turn the response into a dictionary
    page = eval(response.content)

    # Step 3: open/create the text file
    file = open(text_file, "a", encoding="utf-8-sig")

    # Step 4: loop over each client to save in the file
    for client in page["data"]:
        file.write(str(client) + "\n")

    # Step 5: close file
    file.close()

    # Step 6: if there's a next page then return it
    if page.get("paging"):
        if page["paging"].get("cursors"):
            if page["paging"]["cursors"].get("after"):
                return page["paging"]["cursors"]["after"]

    # Step 7: if there's no next page then stop
    return None


def load_all_clients():
    global receiving_data

    # Step 1: trigger the looper by requesting the first page
    after = load_page_of_clients(receiving_data)

    # Step 2: as long as there's a next page keep loading & processing it
    while after:
        receiving_data.update({"after": after})
        after = load_page_of_clients(receiving_data)


def message_clients(amount=None, message="heylo"):

    # Step 1: open the client list file
    file = open(text_file, "r", encoding="utf-8-sig")

    # Step 2: extract the specified amount of clients (all if none specified)
    ids = []
    amount_of_clients = 0
    for line in file.read().split("\n")[:-1]:
        client = eval(line)

        # Step 2.1: only append clients whom's accounts are still valid
        if client["participants"]["data"][0]["name"]!="Facebook User":
            ids.append(client["participants"]["data"][0]["id"])

            # Step 2.2: count the clients & break if we've passed the limit
            amount_of_clients += 1

            if amount:
                if amount_of_clients >= amount:
                    break

    # Step 3: close the file
    file.close()

    print("{} clients will be messaged".format(total_clients))

    # Step 4: prepare payload
    global sending_data
    sending_data["message"].update({"text":message})

    # Step 5: loop over clients
    for id in ids:

        # Step 5.1: first set this client's id
        payload["recipient"].update({"id": id})

        # Step 5.2: second send the post request
        response = requests.post(SENDING_API_URL, params=sending_auth, json=sending_data)

        # Step 5.3: if response doesn't seem like it was accepted then stop
        if response.content.find("recipient_id")==-1:
            print("failed to message {} with response {}".format(id, response.content))
            return


    print("Successfully messaged {} clients".format(total_clients))


#load_all_clients()
#message_clients(amount=None, message="hey boys")
