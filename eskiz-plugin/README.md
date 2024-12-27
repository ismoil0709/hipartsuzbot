
# Eskiz Plugin

Eskiz Plugin is a Java-based tool designed to integrate with the [Eskiz SMS API](https://eskiz.uz/). This plugin enables developers to send SMS, manage templates, track SMS statuses, and generate reports with ease. It serves as an efficient bridge for automating SMS communications in any Java application.

---

## Features

- **Send SMS:** Seamlessly send SMS messages to users.
- **Template Management:** Create and manage SMS templates for consistent messaging.
- **Report Generation:** Fetch detailed reports for sent SMS by date or month.
- **Status Tracking:** Monitor the delivery status of sent SMS messages.
- **User Data Management:** Retrieve user-related data for better insights.

---

## Project Structure

- **Configuration:**
  - `WebClientConfig.java`: Configures the WebClient for API interactions.
  - `EskizProperties.java`: Holds customizable properties related to Eskiz API.

- **DTO (Data Transfer Objects):**
  - `MessageDto.java`, `TokenHolderDTO.java`: Transfer data between layers.
  - Request and Response DTOs, e.g., `SendSmsResponseDTO`, `TemplateRequestDTO`.

- **Services:**
  - `SendSmsService`, `ReportService`: Core services for sending SMS and generating reports.
  - Implementations: `SendSmsServiceImpl`, `ReportServiceImpl`.

- **Utility:**
  - `HelperUtil.java`: Contains helper methods to simplify common tasks.

- **Storage:**
  - `TokenStorage.java`: Securely manages API tokens for authorization.

- **Enums:**
  - `Constants.java`: Defines reusable constants for the application.

---

## Prerequisites

Ensure the following tools and environments are available:

- **Java** (version 11 or higher)
- **Maven** (for dependency management)
- Access to [Eskiz API](https://eskiz.uz/) credentials:
  - `Email`
  - `Password`
  - `Sender Name`
  - `Callback URL`

---

## Installation

1. Clone the repository:

   ```bash
   git clone https://github.com/yourusername/eskiz-plugin.git
   cd eskiz-plugin
   ```

2. Configure the application:

   Update the `application.yml` file with your Eskiz API credentials:

   ```yaml
   eskiz:
     simulate: false           # Set to true for testing without actually sending SMS
     email: 'your-email'       # Your registered email on Eskiz
     password: 'your-password' # Your Eskiz account password
     sender-name: 'your-sender-name' # The sender name or ID used in SMS
     callback-url: 'your-callback-url' # Optional callback URL for delivery reports
   ```

3. Build the project using Maven:

   ```bash
   mvn clean install
   ```

4. Include the plugin in your Java application by importing the appropriate classes.

---

## Usage

### Sending SMS

Use `SendSmsService` to send SMS messages. Example:

```java
@Autowired
private SendSmsService sendSmsService;

public void sendMessage() {
    SendSmsResponseDTO response = sendSmsService.sendSms("998901234567", "Hello, this is a test message.");
    System.out.println("Message sent: " + response.getMessage());
}
```

### Fetching Reports

Use `ReportService` to fetch SMS reports. Example:

```java
@Autowired
private ReportService reportService;

public void getMonthlyReport() {
    ReportByMonthResponseDTO report = reportService.getReportByMonth("2024-11");
    System.out.println("Total Messages Sent: " + report.getTotalMessages());
}
```

### Managing Templates

Use `TemplateService` to manage SMS templates. Example:

```java
@Autowired
private TemplateService templateService;

public void createTemplate() {
    TemplateResponseDTO template = templateService.createTemplate("Greetings", "Hello, welcome to our service!");
    System.out.println("Template ID: " + template.getId());
}
```

---

## Contribution

We welcome contributions! Please follow these steps:

1. Fork the repository.
2. Create a new branch (`feature/my-feature`).
3. Commit your changes.
4. Push to the branch.
5. Open a Pull Request.

---

## License

This project is licensed under the **MIT License**. See the `LICENSE` file for details.

---

## Contact

For any issues or inquiries, please contact:

- **Developer Name:** Javohir Yallayev
- **Email:** javoxiryallayev1@gmail.com  
- **GitHub:** [javohirdeveloper0612](https://github.com/javohirdeveloper0612)

---
