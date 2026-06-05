# 🚀 AI Resume Analyzer (ATS Optimizer)

[![Live Demo](https://img.shields.io/badge/Live_Demo-View_Project-success?style=for-the-badge&logo=vercel)](https://ai-resume-analys-git-ea6168-anupamkumarpanditofficials-projects.vercel.app/)
[![React](https://img.shields.io/badge/React-20232A?style=for-the-badge&logo=react&logoColor=61DAFB)](#)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-F2F4F9?style=for-the-badge&logo=spring-boot)](https://spring.io/projects/spring-boot)
[![Gemini AI](https://img.shields.io/badge/Gemini_AI-4285F4?style=for-the-badge&logo=google&logoColor=white)](#)
[![MongoDB](https://img.shields.io/badge/MongoDB-4EA94B?style=for-the-badge&logo=mongodb&logoColor=white)](#)

A Full-Stack AI-powered Applicant Tracking System (ATS) optimization tool. It analyzes resumes against job descriptions, calculates ATS scores, and provides actionable, STAR-method-based feedback to help candidates increase their interview shortlisting chances.

---

## 🎯 The Problem It Solves
Many highly qualified candidates face rejection simply because their resumes aren't optimized for automated Applicant Tracking Systems (ATS). Existing solutions are often paid or lack deep context analysis. 
**This project solves that by:**
- Extracting exact missing and matched keywords based on the JD context.
- Providing actionable rewrites for weak resume bullet points using the **STAR method**.
- Giving a quantifiable ATS match score.

---

## ✨ Key Features & Technical Highlights

- **Intelligent PDF Parsing:** Implemented `Apache PDFBox` to robustly extract text from complex resume layouts without losing structural context.
- **AI Integration (Gemini 3.5 Flash):** Prompt-engineered a system instruction to force deterministic, structured JSON responses from the LLM, preventing hallucination.
- **Security & Rate Limiting:** Built a custom Spring Boot `Filter` to implement IP-based rate limiting (5 req/min) to protect the Gemini API quota and prevent abuse.
- **Pagination & Caching:** Designed MongoDB aggregations and implemented Spring Data pagination to load historical analysis results efficiently.
- **Production-Ready Configuration:** Segregated environment variables for seamless deployment, with dynamic port bindings and strict CORS configurations.

---

## 💻 Tech Stack

### Frontend
- **Framework:** React.js, TypeScript, Vite
- **Styling:** Tailwind CSS, Framer Motion (for micro-animations)
- **Deployment:** Vercel

### Backend
- **Framework:** Java 17, Spring Boot 3.3.x
- **Database:** MongoDB (Spring Data MongoDB)
- **AI / LLM:** Google Gemini REST API (gemini-3.5-flash)
- **Utilities:** Apache PDFBox, Jackson, Lombok
- **Deployment:** Dockerized and deployed on Render

---

## 🏗️ System Architecture

1. **Client** uploads a PDF and pastes the Job Description.
2. **Spring Boot Backend** validates the file, extracts text, and constructs a robust prompt.
3. Request is sent to **Gemini API** for semantic analysis.
4. Response is parsed, validated, and saved to **MongoDB Atlas**.
5. **React Client** renders the actionable insights with dynamic charts and feedback UI.

---

## 🚀 Live Demo

**Frontend (Vercel):** [Live Link](https://ai-resume-analys-git-ea6168-anupamkumarpanditofficials-projects.vercel.app/)  
*(Please note: Since the backend is hosted on Render's free tier, the first request might take 30-50 seconds to spin up the server if it was idle.)*

---

## 🛠️ How to Run Locally

### Prerequisites
- Node.js (v18+)
- Java 17+
- MongoDB instance (Local or Atlas)
- Gemini API Key

### Backend Setup
```bash
cd backend
# Create a .env.properties file with your credentials
echo "GEMINI_API_KEY=your_key_here" > .env.properties
echo "MONGODB_URI=mongodb://localhost:27017/resume_analyzer" >> .env.properties
```
# Run the Spring Boot application
./mvnw spring-boot:run
```
```
### Frontend setup
```
cd frontend
npm install
npm run dev
```
