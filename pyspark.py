from pyspark.sql import SparkSession
import re

# Configuration
STORAGE_ACCOUNT_NAME = "your_storage_account_name"
CONTAINER_NAME = "your_container_name"
BASE_PATH = f"wasbs://{CONTAINER_NAME}@{STORAGE_ACCOUNT_NAME}.blob.core.windows.net/folder1/folder2/"

# Regex Patterns
DATE_FOLDER_PATTERN = r"2024-\d{2}-\d{2}/"  # Matches "2024-MM-DD/" folders
SUCCESS_PATTERN = r"_SUCCESS_RTG_\d+"
MISSING_FILE_PATTERN = r"93005_SGAS_\d{8}_SGAS0RTG_OrderEvents_000001\.csv"

# Initialize Spark Session
spark = SparkSession.builder.appName("AzureBlobScan").getOrCreate()

# List of folders with issues
folders_with_issues = []

def check_folder(folder_path):
    """Check if a folder contains the required files."""
    try:
        # List files in the folder
        files_df = spark.read.format("binaryFile").load(folder_path)
        files = files_df.select("path").rdd.map(lambda row: row["path"]).collect()

        found_success = any(re.search(SUCCESS_PATTERN, file) for file in files)
        found_specific_file = any(re.search(MISSING_FILE_PATTERN, file) for file in files)

        if found_success and not found_specific_file:
            folders_with_issues.append(folder_path)

    except Exception as e:
        print(f"Error accessing folder {folder_path}: {e}")

# Scan for folders under the base path
all_folders = spark.read.format("binaryFile").load(BASE_PATH).select("path").rdd.map(lambda row: row["path"]).collect()
unique_folders = set("/".join(path.split("/")[:-1]) + "/" for path in all_folders)

# Filter folders matching the 2024-MM-DD pattern
date_folders = [folder for folder in unique_folders if re.match(DATE_FOLDER_PATTERN, folder)]

# Check each folder
for folder in date_folders:
    check_folder(folder)

# Output results
if folders_with_issues:
    print("\nFolders with _SUCCESS_RTG files but missing the specific file:")
    for folder in folders_with_issues:
        print(folder)
else:
    print("\nNo issues found.")
