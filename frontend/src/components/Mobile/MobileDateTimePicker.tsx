import React, { useState, useEffect } from 'react';
import { Modal } from 'antd';
import dayjs, { Dayjs } from 'dayjs';
import './MobileDateTimePicker.css';

interface MobileDateTimePickerProps {
  visible: boolean;
  value?: Dayjs | null;
  onConfirm: (date: Dayjs) => void;
  onCancel: () => void;
  title?: string;
  showTime?: boolean;
}

const MobileDateTimePicker: React.FC<MobileDateTimePickerProps> = ({
  visible,
  value,
  onConfirm,
  onCancel,
  title = '选择日期',
  showTime = true
}) => {
  const [selectedDate, setSelectedDate] = useState<Dayjs>(value || dayjs());
  const [currentMonth, setCurrentMonth] = useState<Dayjs>(value || dayjs());
  const [activeTab, setActiveTab] = useState<'date' | 'time'>('date');
  const [selectedTime, setSelectedTime] = useState({
    hour: (value || dayjs()).hour(),
    minute: (value || dayjs()).minute()
  });

  useEffect(() => {
    if (value) {
      setSelectedDate(value);
      setCurrentMonth(value);
      setSelectedTime({
        hour: value.hour(),
        minute: value.minute()
      });
    }
  }, [value]);

  // 获取当前月份的日历数据
  const getCalendarDays = () => {
    const startOfMonth = currentMonth.startOf('month');
    const endOfMonth = currentMonth.endOf('month');
    const startOfWeek = startOfMonth.startOf('week');
    const endOfWeek = endOfMonth.endOf('week');
    
    const days = [];
    let current = startOfWeek;
    
    while (current.isBefore(endOfWeek) || current.isSame(endOfWeek, 'day')) {
      days.push(current);
      current = current.add(1, 'day');
    }
    
    return days;
  };

  // 处理日期选择
  const handleDateSelect = (date: Dayjs) => {
    const newDate = date.hour(selectedTime.hour).minute(selectedTime.minute);
    setSelectedDate(newDate);
  };

  // 处理时间选择
  const handleTimeChange = (type: 'hour' | 'minute', value: number) => {
    const newTime = { ...selectedTime, [type]: value };
    setSelectedTime(newTime);
    
    const newDate = selectedDate.hour(newTime.hour).minute(newTime.minute);
    setSelectedDate(newDate);
  };

  // 确认选择
  const handleConfirm = () => {
    const finalDate = selectedDate.hour(selectedTime.hour).minute(selectedTime.minute);
    onConfirm(finalDate);
  };

  // 生成时间选项
  const generateTimeOptions = (max: number, step: number = 1) => {
    const options = [];
    for (let i = 0; i < max; i += step) {
      options.push(i);
    }
    return options;
  };

  const weekDays = ['日', '一', '二', '三', '四', '五', '六'];
  const months = ['一月', '二月', '三月', '四月', '五月', '六月', 
                 '七月', '八月', '九月', '十月', '十一月', '十二月'];

  return (
    <Modal
      open={visible}
      onCancel={onCancel}
      footer={null}
      width="100%"
      style={{ top: 0, padding: 0 }}
      className="mobile-datetime-picker-modal"
      maskClosable={false}
    >
      <div className="mobile-datetime-picker">
        {/* 头部 */}
        <div className="picker-header">
          <button className="header-btn cancel" onClick={onCancel}>
            取消
          </button>
          <div className="header-tabs">
            <button 
              className={`tab-btn ${activeTab === 'date' ? 'active' : ''}`}
              onClick={() => setActiveTab('date')}
            >
              日期
            </button>
            {showTime && (
              <button 
                className={`tab-btn ${activeTab === 'time' ? 'active' : ''}`}
                onClick={() => setActiveTab('time')}
              >
                时间段
              </button>
            )}
          </div>
          <button className="header-btn confirm" onClick={handleConfirm}>
            完成
          </button>
        </div>

        {/* 日期选择器 */}
        {activeTab === 'date' && (
          <div className="date-picker">
            {/* 月份导航 */}
            <div className="month-header">
              <button 
                className="nav-btn"
                onClick={() => setCurrentMonth(currentMonth.subtract(1, 'month'))}
              >
                ‹
              </button>
              <span className="month-title">
                {months[currentMonth.month()]}
              </span>
              <button 
                className="nav-btn"
                onClick={() => setCurrentMonth(currentMonth.add(1, 'month'))}
              >
                ›
              </button>
            </div>

            {/* 星期标题 */}
            <div className="week-header">
              {weekDays.map(day => (
                <div key={day} className="week-day">{day}</div>
              ))}
            </div>

            {/* 日期网格 */}
            <div className="date-grid">
              {getCalendarDays().map((date, index) => {
                const isCurrentMonth = date.month() === currentMonth.month();
                const isSelected = date.isSame(selectedDate, 'day');
                const isToday = date.isSame(dayjs(), 'day');
                
                return (
                  <div
                    key={index}
                    className={`date-cell ${!isCurrentMonth ? 'other-month' : ''} 
                               ${isSelected ? 'selected' : ''} 
                               ${isToday ? 'today' : ''}`}
                    onClick={() => isCurrentMonth && handleDateSelect(date)}
                  >
                    <div className="date-number">{date.date()}</div>
                    <div className="date-lunar">
                      {isToday ? '今天' : 
                       date.date() === 1 ? months[date.month()] : 
                       getLunarDay(date)}
                    </div>
                  </div>
                );
              })}
            </div>
          </div>
        )}

        {/* 时间选择器 */}
        {activeTab === 'time' && showTime && (
          <div className="time-picker">
            <div className="time-section">
              <div className="time-label">
                <span className="time-icon">🕐</span>
                时间
              </div>
              <div className="time-selectors">
                <div className="time-selector">
                  <div className="selector-label">小时</div>
                  <div className="selector-wheel">
                    {generateTimeOptions(24).map(hour => (
                      <div
                        key={hour}
                        className={`time-option ${selectedTime.hour === hour ? 'selected' : ''}`}
                        onClick={() => handleTimeChange('hour', hour)}
                      >
                        {hour.toString().padStart(2, '0')}
                      </div>
                    ))}
                  </div>
                </div>
                <div className="time-separator">:</div>
                <div className="time-selector">
                  <div className="selector-label">分钟</div>
                  <div className="selector-wheel">
                    {generateTimeOptions(60, 15).map(minute => (
                      <div
                        key={minute}
                        className={`time-option ${selectedTime.minute === minute ? 'selected' : ''}`}
                        onClick={() => handleTimeChange('minute', minute)}
                      >
                        {minute.toString().padStart(2, '0')}
                      </div>
                    ))}
                  </div>
                </div>
              </div>
            </div>

            {/* 其他选项 */}
            <div className="other-options">
              <div className="option-item">
                <span className="option-icon">🔔</span>
                <span className="option-label">提醒</span>
                <span className="option-value">无 ›</span>
              </div>
              <div className="option-item">
                <span className="option-icon">🔄</span>
                <span className="option-label">重复</span>
                <span className="option-value">无 ›</span>
              </div>
            </div>
          </div>
        )}
      </div>
    </Modal>
  );
};

// 简单的农历日期显示（这里用简化版本）
const getLunarDay = (date: Dayjs): string => {
  const day = date.date();
  if (day <= 10) return `初${day === 10 ? '十' : ['', '一', '二', '三', '四', '五', '六', '七', '八', '九'][day]}`;
  if (day <= 20) return `${day === 20 ? '二十' : '十' + ['', '一', '二', '三', '四', '五', '六', '七', '八', '九'][day - 10]}`;
  if (day <= 30) return `${day === 30 ? '三十' : '廿' + ['', '一', '二', '三', '四', '五', '六', '七', '八', '九'][day - 20]}`;
  return '卅一';
};

export default MobileDateTimePicker;