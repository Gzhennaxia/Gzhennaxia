import React, { useState, useEffect } from 'react';
import { List, Card, Typography, Space, Button, Tag, Modal, Input, Popconfirm, message, Empty } from 'antd';
import { 
  BookOutlined, 
  EditOutlined, 
  DeleteOutlined, 
  EyeOutlined,
  SearchOutlined,
  FilterOutlined
} from '@ant-design/icons';
import { QuestionBankItem } from '../../types/QuestionBank';
import { questionBankService } from '../../services/questionBankService';

const { Title, Text, Paragraph } = Typography;
const { Search } = Input;

const QuestionBankList: React.FC = () => {
  const [questions, setQuestions] = useState<QuestionBankItem[]>([]);
  const [filteredQuestions, setFilteredQuestions] = useState<QuestionBankItem[]>([]);
  const [loading, setLoading] = useState<boolean>(false);
  const [searchText, setSearchText] = useState<string>('');
  const [selectedTags, setSelectedTags] = useState<string[]>([]);
  const [previewModalVisible, setPreviewModalVisible] = useState<boolean>(false);
  const [selectedQuestion, setSelectedQuestion] = useState<QuestionBankItem | null>(null);

  useEffect(() => {
    loadQuestions();
  }, []);

  useEffect(() => {
    filterQuestions();
  }, [questions, searchText, selectedTags]);

  const loadQuestions = () => {
    setLoading(true);
    try {
      const allQuestions = questionBankService.getQuestionBankItems();
      setQuestions(allQuestions);
    } catch (error) {
      console.error('加载题库失败:', error);
      message.error('加载题库失败');
    } finally {
      setLoading(false);
    }
  };

  const filterQuestions = () => {
    let filtered = questions;

    // 按搜索文本过滤
    if (searchText.trim()) {
      const searchLower = searchText.toLowerCase();
      filtered = filtered.filter(question =>
        question.title.toLowerCase().includes(searchLower) ||
        question.content.toLowerCase().includes(searchLower) ||
        question.tags.some(tag => tag.toLowerCase().includes(searchLower))
      );
    }

    // 按标签过滤
    if (selectedTags.length > 0) {
      filtered = filtered.filter(question =>
        selectedTags.every(tag => question.tags.includes(tag))
      );
    }

    setFilteredQuestions(filtered);
  };

  const handleDelete = (id: string) => {
    if (questionBankService.deleteQuestionBankItem(id)) {
      setQuestions(questions.filter(q => q.id !== id));
      message.success('题目删除成功');
    } else {
      message.error('题目删除失败');
    }
  };

  const handlePreview = (question: QuestionBankItem) => {
    setSelectedQuestion(question);
    setPreviewModalVisible(true);
  };

  const getAllTags = () => {
    const allTags = new Set<string>();
    questions.forEach(question => {
      question.tags.forEach(tag => allTags.add(tag));
    });
    return Array.from(allTags);
  };

  const handleTagClick = (tag: string) => {
    if (selectedTags.includes(tag)) {
      setSelectedTags(selectedTags.filter(t => t !== tag));
    } else {
      setSelectedTags([...selectedTags, tag]);
    }
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

  const getDocumentName = (documentId: string) => {
    const document = questionBankService.getDocument(documentId);
    return document ? document.name : '未知文档';
  };

  const allTags = getAllTags();

  return (
    <div className="question-bank-list">
      <div style={{ marginBottom: '24px' }}>
        <Title level={2}>题库管理</Title>
        <Text type="secondary">共 {questions.length} 道题目</Text>
      </div>

      {/* 搜索和过滤 */}
      <Card style={{ marginBottom: '24px' }}>
        <Space direction="vertical" style={{ width: '100%' }} size="middle">
          <Search
            placeholder="搜索题目标题、内容或标签"
            value={searchText}
            onChange={(e) => setSearchText(e.target.value)}
            style={{ width: '100%' }}
            enterButton={<SearchOutlined />}
          />

          {allTags.length > 0 && (
            <div>
              <Text strong style={{ marginRight: '12px' }}>
                <FilterOutlined /> 按标签筛选:
              </Text>
              <Space wrap>
                {allTags.map(tag => (
                  <Tag
                    key={tag}
                    color={selectedTags.includes(tag) ? 'blue' : 'default'}
                    style={{ cursor: 'pointer' }}
                    onClick={() => handleTagClick(tag)}
                  >
                    {tag}
                  </Tag>
                ))}
              </Space>
              {selectedTags.length > 0 && (
                <Button
                  type="link"
                  size="small"
                  onClick={() => setSelectedTags([])}
                  style={{ padding: 0, marginLeft: '12px' }}
                >
                  清除筛选
                </Button>
              )}
            </div>
          )}
        </Space>
      </Card>

      {/* 题目列表 */}
      {filteredQuestions.length === 0 ? (
        <Empty
          image={Empty.PRESENTED_IMAGE_SIMPLE}
          description={
            questions.length === 0 
              ? "还没有任何题目，请先上传PDF并提取内容" 
              : "没有找到匹配的题目"
          }
        />
      ) : (
        <List
          loading={loading}
          dataSource={filteredQuestions}
          renderItem={(question) => (
            <List.Item style={{ padding: 0, marginBottom: '16px' }}>
              <Card
                className="question-item"
                actions={[
                  <Button
                    type="text"
                    icon={<EyeOutlined />}
                    onClick={() => handlePreview(question)}
                  >
                    预览
                  </Button>,
                  <Button
                    type="text"
                    icon={<EditOutlined />}
                    onClick={() => message.info('编辑功能开发中...')}
                  >
                    编辑
                  </Button>,
                  <Popconfirm
                    title="确定要删除这道题目吗？"
                    onConfirm={() => handleDelete(question.id)}
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
                <div className="question-header">
                  <Title level={4} className="question-title">
                    <BookOutlined style={{ marginRight: '8px', color: '#1890ff' }} />
                    {question.title}
                  </Title>
                  <div className="question-meta">
                    <Text type="secondary">
                      来源: {getDocumentName(question.documentId)} - 第{question.pageNumber}页
                    </Text>
                    <Text type="secondary">
                      创建时间: {formatDate(question.createdDate)}
                    </Text>
                  </div>
                </div>

                {question.content && (
                  <Paragraph
                    className="question-content"
                    ellipsis={{ rows: 3, expandable: true, symbol: '展开' }}
                  >
                    {question.content}
                  </Paragraph>
                )}

                {question.images && question.images.length > 0 && (
                  <div className="question-images">
                    {question.images.slice(0, 4).map((image, index) => (
                      <img
                        key={index}
                        src={image}
                        alt={`题目图片 ${index + 1}`}
                        className="question-image"
                        onClick={() => {
                          Modal.info({
                            title: `图片 ${index + 1}`,
                            content: <img src={image} style={{ width: '100%' }} alt="" />,
                            width: 800,
                          });
                        }}
                      />
                    ))}
                    {question.images.length > 4 && (
                      <div className="question-image" style={{ 
                        display: 'flex', 
                        alignItems: 'center', 
                        justifyContent: 'center',
                        background: '#f5f5f5',
                        color: '#999'
                      }}>
                        +{question.images.length - 4}
                      </div>
                    )}
                  </div>
                )}

                {question.tags && question.tags.length > 0 && (
                  <div className="question-tags">
                    {question.tags.map(tag => (
                      <Tag key={tag} color="blue">
                        {tag}
                      </Tag>
                    ))}
                  </div>
                )}
              </Card>
            </List.Item>
          )}
        />
      )}

      {/* 预览模态框 */}
      <Modal
        title="题目预览"
        open={previewModalVisible}
        onCancel={() => setPreviewModalVisible(false)}
        footer={[
          <Button key="close" onClick={() => setPreviewModalVisible(false)}>
            关闭
          </Button>
        ]}
        width={800}
      >
        {selectedQuestion && (
          <Space direction="vertical" style={{ width: '100%' }} size="middle">
            <div>
              <Title level={3}>{selectedQuestion.title}</Title>
              <Text type="secondary">
                来源: {getDocumentName(selectedQuestion.documentId)} - 第{selectedQuestion.pageNumber}页
              </Text>
            </div>

            {selectedQuestion.content && (
              <div>
                <Title level={5}>内容</Title>
                <div style={{ 
                  background: '#f9f9f9', 
                  padding: '16px', 
                  borderRadius: '6px',
                  whiteSpace: 'pre-wrap'
                }}>
                  {selectedQuestion.content}
                </div>
              </div>
            )}

            {selectedQuestion.images && selectedQuestion.images.length > 0 && (
              <div>
                <Title level={5}>图片</Title>
                <Space wrap>
                  {selectedQuestion.images.map((image, index) => (
                    <img
                      key={index}
                      src={image}
                      alt={`题目图片 ${index + 1}`}
                      style={{ 
                        maxWidth: '200px', 
                        maxHeight: '200px',
                        border: '1px solid #d9d9d9',
                        borderRadius: '6px',
                        cursor: 'pointer'
                      }}
                      onClick={() => {
                        Modal.info({
                          title: `图片 ${index + 1}`,
                          content: <img src={image} style={{ width: '100%' }} alt="" />,
                          width: 800,
                        });
                      }}
                    />
                  ))}
                </Space>
              </div>
            )}

            {selectedQuestion.tags && selectedQuestion.tags.length > 0 && (
              <div>
                <Title level={5}>标签</Title>
                <Space wrap>
                  {selectedQuestion.tags.map(tag => (
                    <Tag key={tag} color="blue">
                      {tag}
                    </Tag>
                  ))}
                </Space>
              </div>
            )}

            <div>
              <Text type="secondary">
                创建时间: {formatDate(selectedQuestion.createdDate)}
              </Text>
            </div>
          </Space>
        )}
      </Modal>
    </div>
  );
};

export default QuestionBankList;