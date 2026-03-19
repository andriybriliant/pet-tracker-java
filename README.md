# 🐾 Pet Tracker - Pet Health Tracking App

Pet Tracker is a native Android mobile application designed to assist pet owners in managing information about their pets on a daily basis. It enables comprehensive tracking of health status, vet visits, vaccinations, and daily activities.

The application was developed as a final project for the "JAVA Computer Course" at Nicolaus Copernicus University in Toruń.

## Author
* **Andrii Bryliant**

## Tech Stack
* **Programming Language:** Java
* **User Interface (UI):** XML
* **Backend as a Service (BaaS):** Firebase (Authentication, Firestore Database, Cloud Storage for images)
* **Image Management:** Glide

## Key Features
* **Cloud Authentication:** Ability to create an account (email/password) and securely log in on any device using Firebase Auth.
* **Profile Management:** Add, edit, and delete pet profiles (handling photos, basic details, breed, weight, neutered/indoor status).
* **Health & Care Records:** Built-in categories (Vaccines, Vet Visits, Medication, Surgeries, Weight) with the ability to create custom, personalized categories.
* **Multilingual Support:** The app automatically adapts to the device's system language or allows manual selection from three supported languages: English, Polish, and Ukrainian.
* **Real-time Synchronization:** All data is seamlessly synced in the cloud using the Firebase Firestore database.

## Database Structure (Firestore)
The application utilizes a NoSQL document-based data model divided into main collections:
* **`users`**: Stores core user data (id, name, email).
* **`pets`**: Stores pet profiles assigned to a specific user, including attributes like weight, type, and profile image URL.
* **`categories`** and **`logs`**: Store custom record categories and specific events/logs (e.g., visit date, description) associated with a given pet.
* **`other_information`**: A specific document containing additional pet preferences (favorite activity and favorite snack).

## Solved Architectural Challenges
During the development process, the following technical challenges were successfully addressed:
1. **User Input Validation:** Preventing database errors and inconsistencies by strictly verifying the correctness of input fields before form submission.
2. **Asynchronous Loading Optimization (Flash of unloaded content):** Implemented loading indicators (spinners) tied directly to Firestore listeners. The loading wheel is displayed until the data is fully fetched from the cloud, significantly improving the User Experience (UX).
