# Xpress Monie

Xpress Monie is a Spring Boot payment gateway application that enables users to make seamless online payments. It integrates with the Xpress Payment Solution API.

## Features
* User Registration: Users can sign up for an account on the platform.
* User Authentication: Users can log in to the platform using their credentials.
* Online Airtime Purchase: Users can buy airtime online.

## Documentation
The application's documentation is available through Swagger. After building the application, you can access the documentation by opening the following URL in your browser: http://localhost:9090/swagger-ui/index.html#/

## Installation
1. Clone the repository:
git clone https://github.com/intelliDean/xpress.git


2. Set the following environment variables in the application configuration file:
* DB_HOST:
* DB_NAME:
* DB_USERNAME:
* DB_PASSWORD:
* DB_PORT:
* DATABASE:
* DIALECT:

* SECRET_KEY:  <a string to sign the jwt>

* sendinblue_api: <brevo api key>
* sendinblue_mail: https://api.sendinblue.com/v3/smtp/email

* app_name: app name
* app_email: app email

* admin_name: name
* admin_email: email
* admin_password: password

* private: <xpress payment solution private key>
* public: <xpress payment solution public key>
* url: "https://billerstest.xpresspayments.com:9603/api/v1/airtime/fulfil"

* access_token: <access token expiration time>
* refresh_token: <refresh token expiration time>


3. Configure the database settings, Brevo API with the API key and mail URL, and obtain the Xpress Payment Solution private and public keys.

4. Build and run the application.

5. Access the application at http://localhost:9090.

## Technologies Used
* Java
* Spring Boot
* Spring Security
* PostgreSQL
* Brevo API
* Xpress Payment Solution API
* Thymeleaf

## Contributing
Contributions are welcome! If you find any issues or have suggestions for improvements, please open an issue or submit a pull request.
