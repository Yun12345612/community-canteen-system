#!/bin/bash

echo "========================================"
echo "  社区食堂系统 - 微信小程序构建脚本"
echo "========================================"
echo ""

echo "[1/3] 检查 Node.js 环境..."
if ! command -v node &> /dev/null; then
    echo "[错误] 未检测到 Node.js，请先安装 Node.js"
    echo "下载地址: https://nodejs.org/"
    exit 1
fi
echo "[✓] Node.js 环境正常"
echo ""

echo "[2/3] 安装依赖包..."
if [ ! -d "node_modules" ]; then
    echo "首次运行，正在安装依赖包，请稍候..."
    npm install
    if [ $? -ne 0 ]; then
        echo "[错误] 依赖安装失败，请检查网络连接"
        exit 1
    fi
else
    echo "[✓] 依赖包已存在"
fi
echo ""

echo "[3/3] 编译微信小程序..."
npm run build:mp-weixin
if [ $? -ne 0 ]; then
    echo "[错误] 编译失败，请查看错误信息"
    exit 1
fi
echo ""

echo "========================================"
echo "  编译完成！"
echo "========================================"
echo ""
echo "编译输出目录: dist/build/mp-weixin"
echo ""
echo "下一步操作:"
echo "1. 打开微信开发者工具"
echo "2. 点击'导入项目'"
echo "3. 选择目录: $(pwd)/dist/build/mp-weixin"
echo "4. 填写项目名称和 AppID（可使用测试号）"
echo "5. 点击'导入'即可运行"
echo ""
echo "========================================"
