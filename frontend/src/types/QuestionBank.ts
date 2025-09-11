export interface PDFDocument {
  id: string;
  name: string;
  file: File;
  uploadDate: Date;
  totalPages: number;
  url?: string;
}

export interface PDFPage {
  pageNumber: number;
  imageUrl?: string;
  extractedText?: string;
  extractedImages?: string[];
}

export interface QuestionBankItem {
  id: string;
  documentId: string;
  pageNumber: number;
  title: string;
  content: string;
  images: string[];
  createdDate: Date;
  tags: string[];
}

export interface ExtractedContent {
  text: string;
  images: string[];
}