import sqlite3

db = "databased.db"
conn = sqlite3.connect(db)


conn.execute("CREATE TABLE IF NOT EXISTS TESTO ( \
            RANDOM_ID INTEGER PRIMARY KEY AUTOINCREMENT, \
            TESTO TEXT);");


conn.execute("INSERT INTO TESTO (TESTO) VALUES ('ah')")

conn.commit()

conn2 = sqlite3.connect(db)

cursor = conn2.execute("SELECT * FROM TESTO")

for row in cursor:
    print(row)

conn.close()
conn2.close()
