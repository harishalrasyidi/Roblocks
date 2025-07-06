# Roblocks

Roblocks is an innovative Android application that combines visual programming through Blockly with AI capabilities and IoT integration. It's designed to provide an interactive learning platform for robotics and programming education.

##  Features

### 1. Visual Programming with Blockly
- Custom Blockly implementation for Arduino programming
- Visual block-based programming interface
- Real-time code generation
- Support for various Arduino components and sensors

### 2. Artificial Intelligence Integration
- Image classification capabilities
- TensorFlow Lite integration for on-device AI processing
- Custom AI model implementation

### 3. Learning Platform
- Interactive learning modules
- Quiz system for assessment
- Video tutorials integration
- Progress tracking

### 4. User Management
- Multi-role support (Admin, Lecturer, Student)
- Google Sign-In integration
- Firebase Authentication

## 🛠 Technology Stack

- **Frontend**: 
  - Jetpack Compose
  - Material Design 3
  - WebView for Blockly integration

- **Backend Integration**:
  - Retrofit for API communication
  - OkHttp for networking
  - Firebase services

- **Database**:
  - Room Database for local storage
  - Firebase for cloud storage

- **AI/ML**:
  - TensorFlow Lite
  - Custom image classification models

- **Authentication**:
  - Firebase Auth
  - Google Sign-In

## 🚀 Getting Started

### Prerequisites
- Android Studio Arctic Fox or later
- JDK 11 or later
- Android SDK with minimum API level 24
- Google Services configuration

### Setup
1. Clone the repository
2. Open the project in Android Studio
3. Sync Gradle files
4. Configure your `google-services.json`
5. Build and run the application

## 📱 Application Architecture

The application follows Clean Architecture principles with MVVM pattern:
- `data`: Contains repositories, data sources, and database implementations
- `domain`: Business logic and use cases
- `ui`: Compose UI components and screens
- `utils`: Helper classes and utilities
- `di`: Dependency injection modules

## 🔧 Configuration

The application uses different URLs for development and production:
- Base URL: `https://roblocks.pythonanywhere.com`
- Classifier URL: `http://10.10.193.199:5000`

##  Dependencies

Major dependencies include:
- Jetpack Compose for UI
- Dagger Hilt for dependency injection
- Retrofit for networking
- Room for local database
- TensorFlow Lite for AI capabilities
- Firebase for authentication and analytics

##  Contributing

Contributions are welcome! Please feel free to submit pull requests.

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 👥 Authors

- Initial work - DaBoRiBo Gang :
    1. Ilham Faisal Ridhotulloh
    2. Muhammad Harish Al-Rasyidi
    3. Muhammad Ridho Firdaus

##  Acknowledgments

- BlocklyDuino for the visual programming foundation
- TensorFlow team for ML tools
- Firebase team for backend services
