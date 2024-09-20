from pyspark.sql import SparkSession

def transfer_data(source_db_url, dest_db_url, source_table, dest_table, db_properties):
    # Create a Spark session
    spark = SparkSession.builder \
        .appName("Database Transfer") \
        .getOrCreate()

    # Read data from the source database
    source_df = spark.read.jdbc(url=source_db_url, table=source_table, properties=db_properties)

    # Show the data (optional)
    source_df.show()

    # Write data to the destination database
    source_df.write.jdbc(url=dest_db_url, table=dest_table, mode='overwrite', properties=db_properties)

    print(f"Transferred data from {source_table} to {dest_table}.")

    # Stop the Spark session
    spark.stop()

# Database connection parameters
source_db_url = "jdbc:postgresql://source_host:5432/source_db"
dest_db_url = "jdbc:postgresql://dest_host:5432/destination_db"

db_properties = {
    "user": "your_username",
    "password": "your_password",
    "driver": "org.postgresql.Driver"
}

# Usage example
transfer_data(source_db_url, dest_db_url, "source_table", "destination_table", db_properties)
