import psycopg2
from psycopg2 import sql

def transfer_data(source_conn_params, dest_conn_params, source_table, dest_table):
    # Connect to the source database
    source_conn = psycopg2.connect(**source_conn_params)
    source_cursor = source_conn.cursor()
    
    # Connect to the destination database
    dest_conn = psycopg2.connect(**dest_conn_params)
    dest_cursor = dest_conn.cursor()

    try:
        # Fetch all data from the source table
        source_cursor.execute(sql.SQL("SELECT * FROM {}").format(sql.Identifier(source_table)))
        rows = source_cursor.fetchall()

        # Get the column names
        column_names = [desc[0] for desc in source_cursor.description]

        # Insert data into the destination table
        insert_query = sql.SQL("INSERT INTO {} ({}) VALUES ({})").format(
            sql.Identifier(dest_table),
            sql.SQL(', ').join(map(sql.Identifier, column_names)),
            sql.SQL(', ').join(sql.Placeholder() * len(column_names))
        )

        dest_cursor.executemany(insert_query, rows)

        # Commit the changes to the destination database
        dest_conn.commit()

        print(f"Transferred {len(rows)} rows from {source_table} to {dest_table}.")

    except Exception as e:
        print(f"An error occurred: {e}")
    
    finally:
        # Close the database connections
        source_cursor.close()
        source_conn.close()
        dest_cursor.close()
        dest_conn.close()

# Usage example
source_db_params = {
    'dbname': 'source_db',
    'user': 'your_username',
    'password': 'your_password',
    'host': 'localhost',
    'port': '5432'
}

dest_db_params = {
    'dbname': 'destination_db',
    'user': 'your_username',
    'password': 'your_password',
    'host': 'localhost',
    'port': '5432'
}

transfer_data(source_db_params, dest_db_params, 'source_table', 'destination_table')
