# 🚀 AI Resume Analyzer (ATS Optimizer)

[![Live Demo](https://img.shields.io/badge/Live_Demo-View_Project-success?style=for-the-badge&logo=vercel)](https://ai-resume-analys-git-ea6168-anupamkumarpanditofficials-projects.vercel.app/)
[![React](https://img.shields.io/badge/React-20232A?style=for-the-badge&logo=react&logoColor=61DAFB)](https://react.dev/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-F2F4F9?style=for-the-badge&logo=spring-boot)](https://spring.io/projects/spring-boot)
[![Gemini AI](https://img.shields.io/badge/Gemini_AI-4285F4?style=for-the-badge&logo=google&logoColor=white)](https://deepmind.google/technologies/gemini/)
[![MongoDB](https://img.shields.io/badge/MongoDB-4EA94B?style=for-the-badge&logo=mongodb&logoColor=white)](https://www.mongodb.com/)

A Full-Stack AI-powered Applicant Tracking System (ATS) optimization tool. It analyzes resumes against job descriptions, calculates ATS scores, and provides actionable, STAR-method-based feedback to help candidates increase their interview shortlisting chances.

<img width="1364" height="595" alt="{09D2B916-7878-425A-B2BE-21364D7332FA}" src="https://github.com/user-attachments/assets/9f8f580c-67e4-4e0c-bb25-d5247cd8502c" />
<img width="1338" height="573" alt="{DF64991F-F153-4216-ACC4-05627DAF4A48}" src="https://github.com/user-attachments/assets/930645d0-c805-44e7-b379-4f87a063fc8a" />
<img width="1156" height="510" alt="{DC35194B-AB78-430F-A026-BFFB398D1F80}" src="https://github.com/user-attachments/assets/0dd1bb8a-8a77-473c-9210-258715ce39e0" />
<img width="1351" height="590" alt="{0F52EFAD-67A9-4C23-8CEE-CD1DC114A3C3}" src="https://github.com/user-attachments/assets/7d13dec7-958a-42f4-b4a9-4140c434be57" />

---

## 🎯 The Problem It Solves
Many highly qualified candidates face rejection simply because their resumes aren't optimized for automated Applicant Tracking Systems (ATS). Existing solutions are often paid or lack deep context analysis. 

**This project solves that by:**
- Extracting exact missing and matched keywords based on the JD context.
- Providing actionable rewrites for weak resume bullet points using the **STAR method**.
- Giving a quantifiable ATS match score.

---

## ✨ Key Features & Technical Highlights

- **Reliable PDF Parsing:** Used `Apache PDFBox` to extract text from resume PDFs while maintaining structural context for the AI.
- **AI Integration (Gemini 1.5 Flash):** Wrote specific prompts to ensure the LLM returns consistent, structured JSON responses, preventing random text outputs (hallucinations).
- **API Rate Limiting:** Implemented a custom Spring Boot `Filter` to restrict requests (5 req/min) based on IP, protecting the free Gemini API quota from abuse.
- **Pagination:** Used Spring Data MongoDB pagination to load past resume analysis results efficiently without overloading the database.
- **Environment Configuration:** Segregated environment variables for seamless deployment, with dynamic port bindings and strict CORS configurations.

---

## 💻 Tech Stack

### Frontend
- **Framework:** React.js, TypeScript, Vite
- **Styling:** Tailwind CSS, Framer Motion (for micro-animations)
- **Deployment:** Vercel

### Backend
- **Framework:** Java 17, Spring Boot 3.3.x
- **Database:** MongoDB (Spring Data MongoDB)
- **AI / LLM:** Google Gemini REST API (gemini-1.5-flash)
- **Utilities:** Apache PDFBox, Jackson, Lombok
- **Deployment:** Dockerized and deployed on Render

---

## 🏗️ System Architecture

1. **Client** uploads a PDF and pastes the Job Description.
2. **Spring Boot Backend** validates the file, extracts text, and constructs the prompt.
3. Request is sent to the **Gemini API** for semantic analysis.
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

 ### 1. Clone the Repository
```bash
git clone <your-github-repo-link-here>
cd <your-repository-folder-name>
```

### Backend Setup
```bash
cd backend

# Create a .env.properties file with your credentials
echo "GEMINI_API_KEY=your_key_here" > .env.properties
echo "MONGODB_URI=mongodb://localhost:27017/resume_analyzer" >> .env.properties

# For Linux/macOS
./mvnw spring-boot:run

# For Windows
mvnw.cmd spring-boot:run
```

### frontend
```bash
cd frontend
npm install
npm run dev
