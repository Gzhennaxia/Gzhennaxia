# 命令行式哔哩哔哩下载器

> [BBDown](https://github.com/nilaoda/BBDown)

## 使用示例

### 下载合集中的某一P

```bash
PS C:\Users\19625\Downloads\BBDown_1.6.3_20240814_win-x64> .\BBDown.exe "https://www.bilibili.com/video/BV1DX3TzJEdY?spm_id_from=333.788.videopod.episodes&vd_source=abdb2003d2e2fee7024b0e7867ee887f&p=1"
```

下载文件会存放在当前目录下，用合集名称新建文件夹，用分P名称新建文件名。

### 下载合集中的指定多P

#### 指定P

```bash
PS C:\Users\19625\Downloads\BBDown_1.6.3_20240814_win-x64> .\BBDown.exe -p 1,2 "https://www.bilibili.com/video/BV1DX3TzJEdY"
```

#### 指定范围

```bash
PS C:\Users\19625\Downloads\BBDown_1.6.3_20240814_win-x64> .\BBDown.exe -p 1-10 "https://www.bilibili.com/video/BV1DX3TzJEdY"
```

