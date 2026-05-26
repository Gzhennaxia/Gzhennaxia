import dayjs, { Dayjs } from 'dayjs';

/** 历史节点分类 */
export type Sp500HistoryEventCategory = '危机' | '政策' | '冲击' | '结构';

/** 标普 500 历史重要节点（叙事参考 Knight Lab TimelineJS 思路，数据静态维护） */
export interface Sp500HistoryEvent {
  /** 唯一标识 */
  id: string;
  /** 事件起始日 yyyy-MM-dd */
  startDate: string;
  /** 事件结束日，单日事件可不传 */
  endDate?: string;
  /** 标题 */
  title: string;
  /** 简要叙述 */
  summary: string;
  /** 分类标签 */
  category: Sp500HistoryEventCategory;
  /** 点击后图表向前后扩展的月数，默认 6 */
  focusPaddingMonths?: number;
}

export const SP500_HISTORY_EVENTS: Sp500HistoryEvent[] = [
  {
    id: 'black-monday-1987',
    startDate: '1987-10-19',
    title: '黑色星期一',
    summary:
      '程序化交易与全球联动抛售共振，道琼斯单日跌约 22%，标普 500 同步遭遇史上最大单日跌幅之一，引发熔断机制讨论。',
    category: '危机',
  },
  {
    id: 'dotcom-peak-2000',
    startDate: '2000-03-10',
    endDate: '2002-10-09',
    title: '互联网泡沫破裂',
    summary:
      '纳斯达克见顶后科技估值崩塌，「.com」企业大批破产，标普 500 进入漫长调整，纳斯达克最大回撤超过 75%。',
    category: '结构',
    focusPaddingMonths: 12,
  },
  {
    id: '911-2001',
    startDate: '2001-09-11',
    title: '9·11 恐怖袭击',
    summary:
      '纽交所闭市近一周，航空、保险板块暴跌，美联储紧急降息，市场短期恐慌后逐步修复。',
    category: '冲击',
  },
  {
    id: 'gfc-2008',
    startDate: '2008-09-15',
    endDate: '2009-03-09',
    title: '次贷危机 / 雷曼破产',
    summary:
      '雷曼兄弟申请破产保护，信贷冻结演变为全球金融海啸，标普 500 跌入深度熊市，各国推出大规模救助与刺激。',
    category: '危机',
    focusPaddingMonths: 9,
  },
  {
    id: 'us-downgrade-2011',
    startDate: '2011-08-05',
    title: '美国主权评级下调',
    summary:
      '标普首次将美国 AAA 主权评级下调，叠加欧债危机，8 月市场出现剧烈波动，VIX 飙升。',
    category: '政策',
  },
  {
    id: 'china-volatility-2015',
    startDate: '2015-08-24',
    title: '全球「黑色星期一」',
    summary:
      'A 股剧烈波动与人民币贬值预期向全球传导，标普 500 单日跌近 4%，新兴市场同步承压。',
    category: '冲击',
  },
  {
    id: 'fed-tightening-2018',
    startDate: '2018-10-03',
    endDate: '2018-12-24',
    title: '美联储紧缩与贸易战',
    summary:
      '2018 年四次加息叠加中美贸易摩擦，鲍威尔称离「中性利率」仍远，四季度标普大幅回调，圣诞夜接近熊市边缘。',
    category: '政策',
    focusPaddingMonths: 4,
  },
  {
    id: 'covid-2020',
    startDate: '2020-02-20',
    endDate: '2020-03-23',
    title: '新冠疫情冲击',
    summary:
      '全球封城与供应链中断，标普以史上最快之一的速度进入熊市；随后大规模财政与货币刺激推动 V 型反弹。',
    category: '危机',
    focusPaddingMonths: 4,
  },
  {
    id: 'fed-hike-bear-2022',
    startDate: '2022-01-03',
    endDate: '2022-10-12',
    title: '美联储激进加息熊市',
    summary:
      '通胀高企下美联储快速加息，成长股估值压缩，2022 年标普与纳斯达克陷入熊市，「无风险利率」重塑资产定价。',
    category: '政策',
    focusPaddingMonths: 6,
  },
  {
    id: 'svb-2023',
    startDate: '2023-03-10',
    endDate: '2023-03-13',
    title: '硅谷银行危机',
    summary:
      '硅谷银行挤兑与关闭引发区域银行恐慌，监管紧急介入，金融板块震荡，市场重新定价利率风险与久期暴露。',
    category: '危机',
    focusPaddingMonths: 3,
  },
];

const CATEGORY_COLOR: Record<Sp500HistoryEventCategory, string> = {
  危机: '#cf1322',
  政策: '#1677ff',
  冲击: '#d48806',
  结构: '#531dab',
};

/** 节点圆点 / 标签颜色 */
export function getSp500EventColor(category: Sp500HistoryEventCategory): string {
  return CATEGORY_COLOR[category];
}

/** 点击节点后图表应聚焦的日期区间 */
export function getSp500EventFocusRange(event: Sp500HistoryEvent): [Dayjs, Dayjs] {
  const pad = event.focusPaddingMonths ?? 6;
  const start = dayjs(event.startDate).subtract(pad, 'month');
  const end = dayjs(event.endDate ?? event.startDate).add(pad, 'month');
  const today = dayjs();
  return [start, end.isAfter(today) ? today : end];
}

/** 事件是否与当前图表区间有交集（用于高亮） */
export function isSp500EventInRange(event: Sp500HistoryEvent, range: [Dayjs, Dayjs]): boolean {
  const eventStart = dayjs(event.startDate);
  const eventEnd = dayjs(event.endDate ?? event.startDate);
  return !eventEnd.isBefore(range[0], 'day') && !eventStart.isAfter(range[1], 'day');
}

/** 时间轴展示用日期文案 */
export function formatSp500EventDate(event: Sp500HistoryEvent): string {
  const start = dayjs(event.startDate);
  if (!event.endDate || event.endDate === event.startDate) {
    return start.format('YYYY-MM-DD');
  }
  const end = dayjs(event.endDate);
  if (start.year() === end.year()) {
    return `${start.format('YYYY-MM')} ~ ${end.format('MM-DD')}`;
  }
  return `${start.format('YYYY-MM')} ~ ${end.format('YYYY-MM')}`;
}
