# Banking System
Requirement: 
 
To create an application to create a bank account with minimal details and then allow to transfer money from one account to other.
System should be able to handle simultaneous transfer requests.
 
Primary Entity: (Account)
Account Name
Balance
 
**Implicit Requirement:** 
 
•	Account can't be managed with Name since customer names are no unique, so account number is mandatory
 
**Non Functional Requirements:**
 
**Security:**  Since the application deals with financial stuffs, security is very important.  

Adding the following steps to ensure that
 
•	Overall application runs on Https.

•	Secret/Password is encrypted in database.

•	Password policy 

     •  a digit must occur at least once
     
     •  a lower case letter must occur at least once
     
     •  an upper case letter must occur at least once
     
     •  a special character must occur at least once
     
     •  no whitespace allowed in the entire string
     
     •  at least 8 characters



For more security, we can consider the following implementation in future,


•	For Data Protection : Data hashing using MD5 or similar Hash algorithm - the user parameters will be hashed and sent to the server. The API will reform the hash string using the received parameters and ensure it matches to received hash string.

• For Strong Authentication : Two Factor Authentication such as Google Authenticator.


**Technical Details:**

The following are the technologies used,
•	Spring Boot
•	Rest web service
•	JPA
•	Microsoft SQL Server
•	Junit
•	H2 DB (for Junit Testing)


## **Solution:**

**Create Account:**

The following are the details required to create account,

• Full Name

• Initial Balance

• Password

The user have to use this password while transfering the account. 
This password will be encrypted and stored in the database.
The account ID will be returned as response on successful account creation.

**End Points details:**

• URL: https://localhost:1207/api/v1/accounts/

• Method: POST

• Sample Parameters: {"fullName":"XXX","balance":"1000","secret":"vcd123"}

**View Account:**

To view the account details. The following are the details required,

• Account ID

**End Points details:**

• URL: https://localhost:1207/api/v1/accounts/{id}

• Method: GET

**Transfer Money:**


Concurrency acheived by locking both sender and receiver account at the database level.

In case of any deadlock issue, the transaction will try to redo the transaction after few seconds delay.

The maximum number of redo the transaction can be configured in the properties file.



The following are the details required to transfer the money,


• From Account

• To Account

• Amount

• Secret

**End Points details:**

• URL: https://localhost:1207/api/v1/accounts/transfer

• Method: POST

• Sample Parameters: {"fromAccount":"45","toAccount":"47","amount":"10","secret":"vcd123"}


## **Things to do before Running the application:**


The following are needs to be take care before running the application,

• Configure the database details in application.properties

• Configure the JPA/Hibernate details based on database selection in application.properties

• Configure the server port in application.properties

• Configure the Keystore details in application.properties.
The existing keystore is self signed. so if running from Postman/RestClient/similar tool, the browser may ask you to add the URL to the exception list as the certificate cannot be verified

• Configure the maximum number of times the transaction will be repeated in case of any failure due to deadlock issue or db issue.

• Configure the secret key. This will be used for encryption before storing the password to the database.






