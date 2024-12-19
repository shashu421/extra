import re
from azure.storage.blob import BlobServiceClient

# Azure Storage account details
STORAGE_ACCOUNT_NAME = "your_storage_account_name"
STORAGE_ACCOUNT_KEY = "your_storage_account_key"
CONTAINER_NAME = "your_container_name"

# Regex patterns
SUCCESS_PATTERN = r"_SUCCESS_RTG_\d+"
MISSING_FILE_PATTERN = r"93005_SGAS_\d{8}_SGAS0RTG_OrderEvents_000001\.csv"

def find_folders_with_issues(storage_account_name, storage_account_key, container_name):
    # Create a BlobServiceClient
    connection_string = (
        f"DefaultEndpointsProtocol=https;AccountName={storage_account_name};"
        f"AccountKey={storage_account_key};EndpointSuffix=core.windows.net"
    )
    blob_service_client = BlobServiceClient.from_connection_string(connection_string)

    # Access the container
    container_client = blob_service_client.get_container_client(container_name)

    # Dictionary to track folders
    folders_with_success = {}
    folders_with_missing_files = []

    print("Scanning container for issues...")

    # List all blobs in the container
    blobs_list = container_client.list_blobs()
    for blob in blobs_list:
        # Extract folder name (assuming folder structure is like "folder_name/blob_name")
        folder_name = "/".join(blob.name.split("/")[:-1])

        # Check for _SUCCESS_RTG pattern
        if re.search(SUCCESS_PATTERN, blob.name):
            folders_with_success[folder_name] = True

        # Check for the specific missing file pattern
        if re.search(MISSING_FILE_PATTERN, blob.name):
            folders_with_success[folder_name] = False

    # Identify folders with issues
    for folder_name, success_only in folders_with_success.items():
        if success_only:
            folders_with_missing_files.append(folder_name)

    # Print folders with issues
    if folders_with_missing_files:
        print("\nFolders with _SUCCESS_RTG files but missing the specific file:")
        for folder in folders_with_missing_files:
            print(folder)
    else:
        print("\nNo issues found.")

if __name__ == "__main__":
    find_folders_with_issues(STORAGE_ACCOUNT_NAME, STORAGE_ACCOUNT_KEY, CONTAINER_NAME)
