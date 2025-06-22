# 📄 Custom Invoice Parsing System (Java-based)

## 🧩 Overview

This project is a **Custom Invoice Parsing System** developed in Java, designed to extract structured data from invoices in various formats and convert it into clean, structured **JSON output**. The system supports parsing of PDFs, Word documents (DOC/DOCX), plain text (TXT), and scanned image formats (JPG, PNG), and includes OCR support for image-based invoices.

This solution was developed by **Ritik Sharma** as a part of a 1-week assignment challenge, focusing on:
- Clean custom parsing (no online parsing libraries)
- Multiformat handling
- High-performance processing
- Scalable and robust architecture

---

## 🎯 Project Objectives

- Build a Java-based custom parser to extract structured invoice data.
- Output the parsed data in JSON format.
- Support multiple file types with OCR for image-based invoices.
- Ensure robust error handling, logging, and multithreading support.
- Provide APIs to upload invoices and retrieve parsed data.

---

## ⚙️ Tech Stack

| Layer | Technology |
|------|-------------|
| Language | Java |
| Framework | Spring Boot |
| PDF Parsing | Apache PDFBox |
| DOC/DOCX Parsing | Apache POI |
| TXT Parsing | Custom Regex/Text Logic |
| OCR | Tesseract OCR (local) |
| Image Preprocessing | Java ImageIO |
| JSON Serialization | Jackson / Gson |
| Logging | Logback |
| Build Tool | Maven |
| Optional DB | MySQL |

---

## 📁 Supported Input Formats

- PDF
- DOC / DOCX
- TXT
- JPG / PNG (scanned invoices)

---

## 🧠 Key Features

### 1. Invoice Input
- Accepts single or bulk uploads via:
  - Folder-based watcher
  - REST API endpoint

### 2. Parsing Logic
- **PDF:** Apache PDFBox
- **Word Docs:** Apache POI
- **TXT:** Custom regex-based logic
- Extracts fields:
  - Invoice Number, Date
  - Vendor & Buyer Info
  - Line Items (description, quantity, unit price, total price)
  - Subtotal, Tax, Discounts, Total Amount
  - Payment Terms

### 3. OCR for Scanned Images
- Uses Tesseract OCR engine
- Preprocessing includes:
  - Deskewing
  - Binarization
  - Noise reduction

### 4. JSON Output Example

```json
{
  "invoice_number": "INV-202301",
  "invoice_date": "2025-01-02",
  "vendor": {
    "name": "Vendor Inc.",
    "address": "123 Business Rd, City, Country",
    "contact": "vendor@business.com"
  },
  "buyer": {
    "name": "Buyer Corp.",
    "address": "456 Market St, City, Country",
    "contact": "buyer@corp.com"
  },
  "line_items": [
    {
      "description": "Service A",
      "quantity": 1,
      "unit_price": 100.0,
      "total_price": 100.0
    },
    {
      "description": "Product B",
      "quantity": 5,
      "unit_price": 20.0,
      "total_price": 100.0
    }
  ],
  "subtotal": 200.0,
  "tax": 20.0,
  "discount": 10.0,
  "total_amount": 210.0
}
