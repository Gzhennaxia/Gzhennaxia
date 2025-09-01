import { useState } from 'react';
import { Calendar, Card } from 'antd';
import type { Dayjs } from 'dayjs';
import './MobileCalendar.css';

const MobileCalendar: React.FC = () => {
  const [selectedDate, setSelectedDate] = useState<Dayjs | null>(null);

  const onSelect = (date: Dayjs) => {
    setSelectedDate(date);
  };

  return (
    <div className="mobile-calendar">
      <Card className="calendar-card">
        <Calendar 
          fullscreen={false}
          onSelect={onSelect}
        />
      </Card>
      {selectedDate && (
        <Card className="selected-date-info">
          <h3>选中日期: {selectedDate.format('YYYY-MM-DD')}</h3>
          <p>暂无任务安排</p>
        </Card>
      )}
    </div>
  );
};

export default MobileCalendar;