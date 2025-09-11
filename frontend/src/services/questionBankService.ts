import { PDFDocument, PDFPage, QuestionBankItem, ExtractedContent } from '../types/QuestionBank';

class QuestionBankService {
  private documents: PDFDocument[] = [];
  private questionBankItems: QuestionBankItem[] = [];

  // 上传PDF文档
  async uploadPDF(file: File): Promise<PDFDocument> {
    const document: PDFDocument = {
      id: Date.now().toString(),
      name: file.name,
      file: file,
      uploadDate: new Date(),
      totalPages: 0, // 将在PDF加载后更新
      url: URL.createObjectURL(file)
    };

    this.documents.push(document);
    return document;
  }

  // 获取所有文档
  getDocuments(): PDFDocument[] {
    return this.documents;
  }

  // 获取文档详情
  getDocument(id: string): PDFDocument | undefined {
    return this.documents.find(doc => doc.id === id);
  }

  // 删除文档
  deleteDocument(id: string): boolean {
    const index = this.documents.findIndex(doc => doc.id === id);
    if (index > -1) {
      // 清理URL对象
      const doc = this.documents[index];
      if (doc.url) {
        URL.revokeObjectURL(doc.url);
      }
      this.documents.splice(index, 1);
      
      // 删除相关的题库项目
      this.questionBankItems = this.questionBankItems.filter(item => item.documentId !== id);
      return true;
    }
    return false;
  }

  // 更新文档页数
  updateDocumentPages(id: string, totalPages: number): void {
    const doc = this.getDocument(id);
    if (doc) {
      doc.totalPages = totalPages;
    }
  }

  // 保存题库项目
  saveQuestionBankItem(item: Omit<QuestionBankItem, 'id' | 'createdDate'>): QuestionBankItem {
    const newItem: QuestionBankItem = {
      ...item,
      id: Date.now().toString(),
      createdDate: new Date()
    };
    
    this.questionBankItems.push(newItem);
    return newItem;
  }

  // 获取题库项目
  getQuestionBankItems(documentId?: string): QuestionBankItem[] {
    if (documentId) {
      return this.questionBankItems.filter(item => item.documentId === documentId);
    }
    return this.questionBankItems;
  }

  // 删除题库项目
  deleteQuestionBankItem(id: string): boolean {
    const index = this.questionBankItems.findIndex(item => item.id === id);
    if (index > -1) {
      this.questionBankItems.splice(index, 1);
      return true;
    }
    return false;
  }
}

export const questionBankService = new QuestionBankService();