import requests

# load sending variables
from values import sending_auth, sending_data, SENDING_API_URL

# load receiving variables
from values import receiving_data, RECEIVING_API_URL

text_file = "output_with_all_info_4.txt"

client_count = 0

def load_page_of_clients(data):
    global client_count

    # Step 1: request 499 clients from facebook
    response = requests.get(RECEIVING_API_URL, params=data)

    # Step 2: turn the response into a dictionary
    page = eval(response.text)

    # Step 3: open/create the text file
    file = open(text_file, "a", encoding="utf-8-sig")

    # Step 4: loop over each client to save in the file
    for ct in page["data"]:
        client = str(ct)
        found = False
        lines_looped = 0

        try:
            file_read = open(text_file, "r", encoding="utf-8-sig")
            for line in file_read.readlines():
                lines_looped += 1
                if eval()["id"]==eval(line)["id"]:
                    found = True
                    break
            file_read.close()
        except:
            pass

        if not found:
            file.write(str(client) + "\n")
        client_count += 1
    # Step 5: close file
    file.close()
    file.close()

    print("clients " + str(client_count))

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
    for line in file.readlines()[:-1]:
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

    print("{} clients will be messaged".format(amount_of_clients))

    # Step 4: prepare payload
    global sending_data
    sending_data["message"].update({"text":message})

    # Step 5: loop over clients
    for id in ids:

        # Step 5.1: first set this client's id
        sending_data["recipient"].update({"id": id})

        # Step 5.2: second send the post request
        response = requests.post(SENDING_API_URL, params=sending_auth, json=sending_data)

        # Step 5.3: if response doesn't seem like it was accepted then stop
        print(response.content)
        # if str(response.content).find("recipient_id")==-1:
        #     print("failed to message {} with response {}".format(id, response.content))
        #     return


    print("Successfully messaged {} clients".format(amount_of_clients))


load_all_clients()
# yes = "متجر مست يحب بكم لشراء المنتجات عبر الإنترنت بتخفيضات رهيبة جديدة، ما عليك إلا إرسال رابط منتجك"
# yes = "متجر مست يعود بتخفيضات رهيبة على طلبياتك من مواقع الانترنت مثل" + "\n" + "Aliexpress, Banggood, Ebay, Wish" + "\n\n" + "ما عليكم إلا إرسال رابط منتجكم لنا"
# message_clients(message=yes)
