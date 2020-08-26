##任务4-2 ： 实现区域增长分割模型 Region Grow Segment


###  给这个segment取名字
/* 去掉rg分割的名字 */ 在 Viewport2d #195

### 编写 ToolRGSelector.java

seed 改为 *Point3i*

### 编写 ToolRGSelector.java

1. ToolRGSelector 新建 segment
3. _varianz_slider 更改时 更新 segment 的 BitMask， 调用 create_region_grow_seg

### 编写 Segment.java

create_region_grow_seg

1. 获取 seed *Point3i*
2. 递归

获取 seed 坐标

## 问题

1. 能否展示一下 rgsegment
2. rg segment 是原始数据上的segment
3. 能否给出后面的4个aufgabe

## TODO
1. 透明度和亮度调节
2. update 按钮大小调节

## Queue

1–>add 增加一个元索，如果队列已满，则抛出一个 
IIIegaISlabEepeplian异常 
2–>remove 移除并返回队列头部的元素，如果队列为空，则抛出一个 
NoSuchElementException异常 
3–>element 返回队列头部的元素，如果队列为空，则抛出一个 
NoSuchElementException异常
4–>offer 添加一个元素并返回true，如果队列已满，则返回false 
5–>poll 移除并返问队列头部的元素，如果队列为空，则返回null 
6–>peek 返回队列头部的元素， 如果队列为空，则返回null
7–>put 添加一个元素，如果队列满，则阻塞 
8–>take 移除并返回队列头部的元素，如果队列为空，则阻塞


## 参考文章

+ [Java3D读取3DMax模型并实现鼠标拖拽、旋转、滚轮缩放等功能](https://www.cnblogs.com/herd/p/6767006.html)
+ [Java3D实例应用-载入3ds 模型](https://www.cnblogs.com/dennisit/archive/2013/05/07/3065479.html)
+ [Java3D应用实例-创建空间几何模型](https://www.cnblogs.com/dennisit/archive/2013/05/06/3063042.html)
+ 