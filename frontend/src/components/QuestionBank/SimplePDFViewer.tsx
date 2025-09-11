import React, { useState, useEffect } from 'react';
import { Button, Space, Typography, Spin, message, Modal, Input, Tag, Divider, Card } from 'antd';
import { 
  ZoomInOutlined, 
  ZoomOutOutlined, 
  DownloadOutlined, 
  FileImageOutlined,
  FileTextOutlined,
  SaveOutlined,
  LeftOutlined,
  RightOutlined,
  EyeOutlined
} from '@ant-design/icons';
import { PDFDocument, ExtractedContent } from '../../types/QuestionBank';
import { questionBankService } from '../../services/questionBankService';

const { Title, Text } = Typography;
const { TextArea } = Input;

interface SimplePDFViewerProps {
  document: PDFDocument | null;
}

const SimplePDFViewer: React.FC<SimplePDFViewerProps> = ({ document }) => {
  const [currentPage, setCurrentPage] = useState<number>(1);
  const [totalPages, setTotalPages] = useState<number>(0);
  const [loading, setLoading] = useState<boolean>(false);
  const [extracting, setExtracting] = useState<boolean>(false);
  const [extractedContent, setExtractedContent] = useState<ExtractedContent | null>(null);
  const [saveModalVisible, setSaveModalVisible] = useState<boolean>(false);
  const [questionTitle, setQuestionTitle] = useState<string>('');
  const [questionContent, setQuestionContent] = useState<string>('');
  const [questionTags, setQuestionTags] = useState<string[]>([]);
  const [newTag, setNewTag] = useState<string>('');
  const [pdfUrl, setPdfUrl] = useState<string>('');

  useEffect(() => {
    if (document) {
      setCurrentPage(1);
      setExtractedContent(null);
      setQuestionTitle('');
      setQuestionContent('');
      setQuestionTags([]);
      setPdfUrl(document.url || '');
      // 模拟获取PDF页数
      setTotalPages(10); // 这里应该从实际PDF获取
      questionBankService.updateDocumentPages(document.id, 10);
    }
  }, [document]);

  const handlePageChange = (page: number) => {
    if (page >= 1 && page <= totalPages) {
      setCurrentPage(page);
      setExtractedContent(null);
    }
  };

  const handleExtractText = async () => {
    if (!document) return;

    setExtracting(true);
    try {
      // 模拟文字提取
      await new Promise(resolve => setTimeout(resolve, 2000));
      
      const mockText = `这是第${currentPage}页提取的文字内容示例。

在实际应用中，这里会显示从PDF页面中提取的真实文字内容。

提取的文字可以用于创建题库项目，包括：
- 题目描述
- 选项内容
- 答案解析
- 相关知识点

您可以编辑这些内容，然后保存到题库中。`;
      
      setExtractedContent({
        text: mockText,
        images: extractedContent?.images || []
      });
      
      setQuestionContent(mockText);
      message.success('文字提取成功');
    } catch (error) {
      console.error('文字提取失败:', error);
      message.error('文字提取失败');
    } finally {
      setExtracting(false);
    }
  };

  const handleExtractImages = async () => {
    if (!document) return;

    setExtracting(true);
    try {
      // 模拟图片提取
      await new Promise(resolve => setTimeout(resolve, 2000));
      
      // 创建示例图片（1x1像素的透明PNG）
      const canvas = window.document.createElement('canvas');
      canvas.width = 200;
      canvas.height = 150;
      const ctx = canvas.getContext('2d');
      if (ctx) {
        // 绘制示例图片
        ctx.fillStyle = '#f0f0f0';
        ctx.fillRect(0, 0, 200, 150);
        ctx.fillStyle = '#666';
        ctx.font = '16px Arial';
        ctx.textAlign = 'center';
        ctx.fillText(`第${currentPage}页图片示例`, 100, 75);
        ctx.fillText(`图片 1`, 100, 95);
      }
      
      const mockImages = [
        canvas.toDataURL('image/png'),
        canvas.toDataURL('image/png')
      ];
      
      setExtractedContent({
        text: extractedContent?.text || '',
        images: mockImages
      });
      
      message.success('图片提取成功');
    } catch (error) {
      console.error('图片提取失败:', error);
      message.error('图片提取失败');
    } finally {
      setExtracting(false);
    }
  };

  const handleDownloadPage = async () => {
    if (!document) return;

    try {
      message.info('下载功能开发中...');
    } catch (error) {
      console.error('下载失败:', error);
      message.error('下载失败');
    }
  };

  const handleSaveQuestion = () => {
    if (!document) return;
    
    if (!questionTitle.trim()) {
      message.error('请输入题目标题');
      return;
    }

    try {
      questionBankService.saveQuestionBankItem({
        documentId: document.id,
        pageNumber: currentPage,
        title: questionTitle,
        content: questionContent,
        images: extractedContent?.images || [],
        tags: questionTags
      });

      message.success('题库项目保存成功');
      setSaveModalVisible(false);
      setQuestionTitle('');
      setQuestionContent('');
      setQuestionTags([]);
    } catch (error) {
      console.error('保存失败:', error);
      message.error('保存失败');
    }
  };

  const handleAddTag = () => {
    if (newTag.trim() && !questionTags.includes(newTag.trim())) {
      setQuestionTags([...questionTags, newTag.trim()]);
      setNewTag('');
    }
  };

  const handleRemoveTag = (tagToRemove: string) => {
    setQuestionTags(questionTags.filter(tag => tag !== tagToRemove));
  };

  if (!document) {
    return (
      <div className="empty-state">
        <FileTextOutlined />
        <div>请选择一个PDF文档进行查看</div>
      </div>
    );
  }

  return (
    <div className="pdf-viewer-container">
      <div className="pdf-sidebar">
        <div style={{ padding: '16px' }}>
          <Title level={5}>{document.name}</Title>
          <Text type="secondary">共 {totalPages} 页</Text>
        </div>
        <Divider style={{ margin: '8px 0' }} />
        <div style={{ padding: '0 16px' }}>
          {Array.from({ length: totalPages }, (_, index) => (
            <Card
              key={index + 1}
              size="small"
              className={`page-thumbnail ${currentPage === index + 1 ? 'selected' : ''}`}
              onClick={() => handlePageChange(index + 1)}
              style={{ 
                marginBottom: '12px', 
                cursor: 'pointer',
                border: currentPage === index + 1 ? '2px solid #1890ff' : '1px solid #d9d9d9'
              }}
            >
              <div style={{ textAlign: 'center', padding: '20px' }}>
                <FileTextOutlined style={{ fontSize: '24px', color: '#666' }} />
                <div style={{ marginTop: '8px', fontSize: '12px' }}>
                  第 {index + 1} 页
                </div>
              </div>
            </Card>
          ))}
        </div>
      </div>

      <div className="pdf-main">
        <div className="pdf-toolbar">
          <Space>
            <Button
              icon={<LeftOutlined />}
              disabled={currentPage <= 1}
              onClick={() => handlePageChange(currentPage - 1)}
            >
              上一页
            </Button>
            <span>
              第 {currentPage} 页 / 共 {totalPages} 页
            </span>
            <Button
              icon={<RightOutlined />}
              disabled={currentPage >= totalPages}
              onClick={() => handlePageChange(currentPage + 1)}
            >
              下一页
            </Button>
          </Space>

          <Space>
            <Button 
              icon={<EyeOutlined />}
              onClick={() => window.open(pdfUrl, '_blank')}
            >
              在新窗口查看PDF
            </Button>
          </Space>
        </div>

        <div className="pdf-content">
          <div className="pdf-page-display">
            {/* PDF预览区域 */}
            <Card style={{ minHeight: '600px', textAlign: 'center' }}>
              <div style={{ padding: '100px 20px' }}>
                <FileTextOutlined style={{ fontSize: '64px', color: '#d9d9d9', marginBottom: '20px' }} />
                <Title level={3} type="secondary">PDF 第 {currentPage} 页</Title>
                <Text type="secondary">
                  点击"在新窗口查看PDF"按钮可以查看完整的PDF文档
                </Text>
                <div style={{ marginTop: '20px' }}>
                  <Button 
                    type="primary" 
                    icon={<EyeOutlined />}
                    onClick={() => window.open(pdfUrl, '_blank')}
                  >
                    查看完整PDF
                  </Button>
                </div>
              </div>
            </Card>

            <div className="page-operations">
              <Button
                type="primary"
                icon={<FileTextOutlined />}
                loading={extracting}
                onClick={handleExtractText}
              >
                提取文字
              </Button>
              <Button
                icon={<FileImageOutlined />}
                loading={extracting}
                onClick={handleExtractImages}
              >
                提取图片
              </Button>
              <Button
                icon={<DownloadOutlined />}
                onClick={handleDownloadPage}
              >
                下载页面
              </Button>
              <Button
                icon={<SaveOutlined />}
                onClick={() => setSaveModalVisible(true)}
                disabled={!extractedContent}
              >
                保存到题库
              </Button>
            </div>

            {extractedContent && (
              <div className="extraction-result">
                {extractedContent.text && (
                  <div>
                    <Title level={4}>提取的文字</Title>
                    <div className="extracted-text">
                      {extractedContent.text}
                    </div>
                  </div>
                )}

                {extractedContent.images && extractedContent.images.length > 0 && (
                  <div>
                    <Title level={4}>提取的图片</Title>
                    <div className="extracted-images">
                      {extractedContent.images.map((image, index) => (
                        <img
                          key={index}
                          src={image}
                          alt={`提取的图片 ${index + 1}`}
                          className="extracted-image"
                          onClick={() => {
                            Modal.info({
                              title: `图片 ${index + 1}`,
                              content: <img src={image} style={{ width: '100%' }} alt="" />,
                              width: 800,
                            });
                          }}
                        />
                      ))}
                    </div>
                  </div>
                )}
              </div>
            )}
          </div>
        </div>
      </div>

      <Modal
        title="保存到题库"
        open={saveModalVisible}
        onOk={handleSaveQuestion}
        onCancel={() => setSaveModalVisible(false)}
        width={600}
        okText="保存"
        cancelText="取消"
      >
        <Space direction="vertical" style={{ width: '100%' }} size="middle">
          <div>
            <Text strong>题目标题 *</Text>
            <Input
              value={questionTitle}
              onChange={(e) => setQuestionTitle(e.target.value)}
              placeholder="请输入题目标题"
              style={{ marginTop: '8px' }}
            />
          </div>

          <div>
            <Text strong>题目内容</Text>
            <TextArea
              value={questionContent}
              onChange={(e) => setQuestionContent(e.target.value)}
              placeholder="请输入题目内容"
              rows={6}
              style={{ marginTop: '8px' }}
            />
          </div>

          <div>
            <Text strong>标签</Text>
            <div style={{ marginTop: '8px' }}>
              <Space wrap>
                {questionTags.map(tag => (
                  <Tag
                    key={tag}
                    closable
                    onClose={() => handleRemoveTag(tag)}
                  >
                    {tag}
                  </Tag>
                ))}
              </Space>
              <div style={{ marginTop: '8px' }}>
                <Input
                  value={newTag}
                  onChange={(e) => setNewTag(e.target.value)}
                  placeholder="添加标签"
                  style={{ width: '200px', marginRight: '8px' }}
                  onPressEnter={handleAddTag}
                />
                <Button onClick={handleAddTag}>添加</Button>
              </div>
            </div>
          </div>

          <div>
            <Text type="secondary">
              来源: {document.name} - 第 {currentPage} 页
            </Text>
          </div>
        </Space>
      </Modal>
    </div>
  );
};

export default SimplePDFViewer;