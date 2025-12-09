# Note Manager – Kotlin Offline-First Notes App

A modular Kotlin-based mobile application for managing notes with offline-first capability and seamless server sync.  
Built with MVVM architecture, Room local database, and a custom backend API.  
Features custom dependency injection, microkernel modularity, and secure authentication with refresh token support.

---

## 🚀 Features
- **Offline-first architecture** with Room database and sync-on-connect  
- **Custom dependency injection** for clean modular design  
- **Microkernel modularity** for scalable feature integration  
- **MVVM pattern** for maintainable UI logic  
- **Login and registration** with refresh token mechanism  
- **Note CRUD operations** with pagination and search  
- **Profile view and logout**  
- **Pull/push sync mechanism** for server updates  

---

## 🏗️ Architecture Overview
- **UI Layer**: Built with MVVM, observing LiveData and ViewModels  
- **Local Storage**: Room database for offline access  
- **Network Layer**: Retrofit for backend API  
- **Sync Engine**: Pull/push mechanism triggered on connectivity  
- **DI Container**: Custom-built for lifecycle-safe dependency management  
- **Microkernel Core**: Enables plug-in modules for features like search, sync, and auth  


---

## 🛠️ Technologies
- Kotlin  
- Room (local DB)  
- Retrofit
- MVVM architecture  
- Custom DI container  
- Microkernel modularity  

---
## ▶️ How to Run
- Set baseurl of backend server in this path: app/src/main/java/com/example/simplenote/core/network/constant/ConstantProvider.kt
