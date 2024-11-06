# Smart_Finance_Tracker
A desktop-based finance management application built with Java Swing and MySQL, enabling users to track expenses, income, and overall balance. Users can create accounts, add financial records, view monthly expenses, and check their current balance.

# Features
## User Authentication:

New users can create a profile with a unique username and password.
Existing users can log in to access their finance data.
## Income and Expense Management:

Add income entries with source, amount, and date.
Add expense entries with name, amount, and date.
## View Financial Records:

Display income and expense tables.
View monthly expenses by specifying a month and year.
Check the total net balance (income - expenses).
## User-Specific Data:

Each user has separate tables for their income and expenses, making data private and user-specific.
# Database Schema
The application connects to a MySQL database with the following structure:

## Users Table: users1

username (VARCHAR) - Primary identifier for the user
password (VARCHAR) - Password for authentication
Income and Expenses Tables: Created per user after profile creation.

username_income - Table to store income records for the user.
Source (VARCHAR) - Source of income
Amount (INTEGER) - Amount of income
Date_of_income (DATE) - Date of income received
username_expenses - Table to store expense records for the user.
Expense_name (VARCHAR) - Name of the expense
Amount (INTEGER) - Amount of the expense
Date_of_expense (DATE) - Date of the expense
# Prerequisites
Java: JDK 8 or later
MySQL: Make sure MySQL server is installed and running
MySQL JDBC Driver: Add the MySQL JDBC driver to the project classpath
# Getting Started
## Set Up the Database:

## Create a MySQL database named finance.
In the mainFrame class, update the MySQL credentials (url, user, pwd) to match your local MySQL configuration.
## Run the Application:

Compile and run financemanagement class.
The login interface should appear. New users can create profiles, while existing users can log in.
Usage
## Creating a Profile:

Enter a unique username and password, then click "Create a new profile."
Once created, the user’s income and expenses tables are set up in the database.
Logging In:

Enter your username and password, then click "Log in."
Managing Finances:

After logging in, users can add income, expenses, view records, and check their balance.
Use the monthly expenses view to filter expenses for a specific month and year.
Exiting the Application:

From the main frame, use the "Exit" button or close the window to terminate the application.
# Class Structure
mainFrame: Manages the main interface for user login, profile creation, and database connection.
addincome: Allows users to add income records.
addexpense: Allows users to add expense records.
viewbalance: Displays the user’s current balance.
MonthlyExpenses: Displays expenses for a specified month and year.
Future Enhancements
Add data export options (e.g., CSV).
Implement password hashing for secure storage.
Add data visualizations for financial insights.
