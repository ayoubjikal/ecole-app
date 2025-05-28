#!/bin/bash

# Script to build and run the Ecole Management Application with MySQL

echo "Building and running Ecole Management Application with MySQL..."
echo "-------------------------------------------------------------"

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "Java is not installed. Please install Java 17 or higher."
    exit 1
fi

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "Maven is not installed. Please install Maven 3.6 or higher."
    exit 1
fi

# Check if MySQL is running
echo "Checking MySQL connection..."
if ! mysql -u root -e "SELECT 1" &>/dev/null; then
    echo "Cannot connect to MySQL. Please make sure MySQL is running."
    echo "You can start MySQL with: sudo service mysql start"
    exit 1
fi

# Create database if it doesn't exist
echo "Creating database if it doesn't exist..."
mysql -u root -e "CREATE DATABASE IF NOT EXISTS ecole_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
if [ $? -ne 0 ]; then
    echo "Failed to create database. Please check your MySQL configuration."
    exit 1
fi

# Build the application
echo "Building the application..."
mvn clean package -DskipTests

# Check if build was successful
if [ $? -ne 0 ]; then
    echo "Build failed. Please check the errors above."
    exit 1
fi

# Run the application
echo "Starting the application..."
java -jar target/management-0.0.1-SNAPSHOT.jar

# This part will only execute if the application is stopped
echo "Application stopped."