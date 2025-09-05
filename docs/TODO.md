1. ~~frontend/src/components/Admin/DictManagement~~
2. 版本号是后端维护的，修改一次更新一次，无需用户干预
3. ~~字典项需要跟换成列表的样式，然后最后一行是个新增的按钮，点击后新增一行，并且字典项的行是可以拖动排序的~~
4. ~~Admin管理界面增加侧边栏，展示菜单~~
5. ~~字典列表页、详情页，增加创建时间和更新时间字段~~



---

1. ~~字典列表操作列，增加启用禁用按钮，列表顶部也增加相应操作按钮~~
2. ~~字典列表操作列中的编辑按钮点击无反应，实现 handleEdit 方法~~
3. DictController 中实现字典删除接口，符合restful，软删除

---

对文件列表及其内容做一个汇总，

增加一个产品说明书的文档，对功能进行说明

---

App.tsx 前端布局，将管理后台抽离出来，不要放在全局layout下

---

1.  [DictList.tsx](..\frontend\src\components\Admin\DictManagement\DictList.tsx)  新增字典弹框，去掉版本号
2. 新增、修改接口，增加修改版本号逻辑
3. 列表头部增加批量启用按钮

---

1.  [DictFormModal.tsx](..\frontend\src\components\Admin\DictManagement\DictFormModal.tsx) 新增字典弹框，去掉版本号
2.  修改dict_item表的字段名
    1. type_code 改为 dict_code
    2. item_key 改为 item_code
    3. item_value 改为 item_name
3.  需要修改的地方
    1.   [DictItem.java](..\backend\src\main\java\com\personal\management\pojo\entity\DictItem.java) 
    2.   [DictManagement](..\frontend\src\components\Admin\DictManagement) 


---

对 dict_type表做以下操作

1. dict_type表名改为 dict
2. code 列名改为 dict_code
3. name 列改为 dict_name

需要修改的地方：

1. com.personal.management.pojo.entity.Dict
2. com.personal.management.service.impl.DictServiceImpl
3. com.personal.management.service.DictService
4. com.personal.management.controller.DictController
5.  [DictManagement](..\frontend\src\components\Admin\DictManagement) 
6.  [dictService.ts](..\frontend\src\services\dictService.ts) 
7.  [dict.ts](..\frontend\src\types\dict.ts) 
8. 其他








