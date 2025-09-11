import React, { useState, useRef } from 'react';
import { Upload, Button, List, Typography, Space, Popconfirm, message } from 'antd';
import { UploadOutlined, FileTextOutlined, DeleteOutlined, EyeOutlined } from '@ant-design/icons';
import { questionBankService } from '../../services/questionBankService';
import { PDFDocument } from '../../types/QuestionBank';

const { Title, Text } = Typography;

interface PDFUploadProps {
  onDocumentSelect: (document: PDFDocument) => void;
}

const PDFUpload: React.FC<PDFUploadProps> = ({ onDocumentSelect }) => {
  const [documents, setDocuments] = useState<PDFDocument[]>(questionBankService.getDocuments());
  const [uploading, setUploading] = useState(false);
  const fileInputRef = useRef<HTMLInputElement>(null);

  const handleFileSelect = async (file: File) => {
    if (file.type !== 'application/pdf') {
      message.error('请选择PDF文件');
      return;
    }

    if (file.size > 50 * 1024 * 1024) { // 50MB限制
      message.error('文件大小不能超过50MB');
      return;
    }

    setUploading(true);
    try {
      const document = await questionBankService.uploadPDF(file);
      setDocuments([...documents, document]);
      message.success('PDF上传成功');
    } catch (error) {
      message.error('PDF上传失败');
      console.error('Upload error:', error);
    } finally {
      setUploading(false);
    }
  };

  const handleDrop = (e: React.DragEvent) => {
    e.preventDefault();
    const files = Array.from(e.dataTransfer.files);
    const pdfFile = files.find(file => file.type === 'application/pdf');
    if (pdfFile) {
      handleFileSelect(pdfFile);
    } else {
      message.error('请拖拽PDF文件');
    }
  };

  const handleDragOver = (e: React.DragEvent) => {
    e.preventDefault();
  };

  const handleFileInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) {
      handleFileSelect(file);
    }
  };

  const handleDelete = (id: string) => {
    if (questionBankService.deleteDocument(id)) {
      setDocuments(documents.filter(doc => doc.id !== id));
      message.success('文档删除成功');
    } else {
      message.error('文档删除失败');
    }
  };

  const formatFileSize = (bytes: number) => {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
  };

  const formatDate = (date: Date) => {
    return date.toLocaleDateString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  return (
    <div className="pdf-upload-container">
      <Title level={2}>PDF文档管理</Title>
      
      <div 
        className="pdf-upload-area"
        onDrop={handleDrop}
        onDragOver={handleDragOver}
        onClick={() => fileInputRef.current?.click()}
      >
        <div className="upload-icon">
          <FileTextOutlined />
        </div>
        <div className="upload-text">点击或拖拽PDF文件到此处上传</div>
        <div className="upload-hint">支持PDF格式，最大50MB</div>
        <Button 
          type="primary" 
          icon={<UploadOutlined />} 
          loading={uploading}
          style={{ marginTop: '16px' }}
        >
          选择文件
        </Button>
      </div>

      <input
        ref={fileInputRef}
        type="file"
        accept=".pdf"
        style={{ display: 'none' }}
        onChange={handleFileInputChange}
      />

      {documents.length > 0 && (
        <div className="document-list">
          <Title level={4}>已上传的文档 ({documents.length})</Title>
          <List
            dataSource={documents}
            renderItem={(document) => (
              <List.Item
                className="document-item"
                actions={[
                  <Button
                    type="text"
                    icon={<EyeOutlined />}
                    onClick={() => onDocumentSelect(document)}
                  >
                    查看
                  </Button>,
                  <Popconfirm
                    title="确定要删除这个文档吗？"
                    onConfirm={() => handleDelete(document.id)}
                    okText="确定"
                    cancelText="取消"
                  >
                    <Button
                      type="text"
                      danger
                      icon={<DeleteOutlined />}
                    >
                      删除
                    </Button>
                  </Popconfirm>
                ]}
              >
                <List.Item.Meta
                  avatar={<FileTextOutlined className="document-icon" />}
                  title={document.name}
                  description={
                    <Space direction="vertical" size={4}>
                      <Text type="secondary">
                        大小: {formatFileSize(document.file.size)}
                      </Text>
                      <Text type="secondary">
                        上传时间: {formatDate(document.uploadDate)}
                      </Text>
                      {document.totalPages > 0 && (
                        <Text type="secondary">
                          页数: {document.totalPages}
                        </Text>
                      )}
                    </Space>
                  }
                />
              </List.Item>
            )}
          />
        </div>
      )}

      {documents.length === 0 && (
        <div className="empty-state">
          <FileTextOutlined />
          <div>还没有上传任何PDF文档</div>
          <div>上传您的第一个PDF文档开始使用题库功能</div>
        </div>
      )}
    </div>
  );
};

export default PDFUpload;