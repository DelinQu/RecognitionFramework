import cv2
import numpy as np
import sys, getopt



def PicFunc(inputFile,outputFile):
    img = cv2.imread(inputFile)

    # 灰度化
    GrayImage = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)

    # 二值化
    ret, thresh1 = cv2.threshold(GrayImage, 210, 255, cv2.THRESH_BINARY)

    # 找出目标轮廓
    contours, hierarchy = cv2.findContours(
        thresh1, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)  # 只检测最外围的轮廓

    # 截取感兴趣的区域
    needimg = cv2.drawContours(img, contours, -1, (0, 255, 0), 1)  # 在原图像上覆盖选取到的轮廓

    # 旋转图像
    contours1 = max(contours, key=cv2.contourArea)
    M = cv2.moments(contours1)
    centerx = int(M['m10']/M['m00'])    # 获取轮廓中心点
    centery = int(M['m01']/M['m00'])    # 获取轮廓中心点

    rect = cv2.minAreaRect(contours1)
    theta = rect[2]

    M1 = cv2.getRotationMatrix2D((centerx,centery), theta, 1)
    result_img = cv2.warpAffine(img, M1, (img.shape[1], img.shape[0]))

    box = cv2.boxPoints(rect)

    # 对ROI区域的轮廓提取和抠图
    w,h = int(rect[1][1]),int(rect[1][0])
    dst_rect = np.array([
                        [0, 0],
                        [w - 1, 0],
                        [w - 1, h - 1],
                        [0, h - 1]],
                        dtype="float32")
    M2 = cv2.getPerspectiveTransform(box, dst_rect)
    result_img = cv2.warpPerspective(img, M2, (w, h))

    cv2.imwrite(outputFile,result_img)


def main(argv):
    inputfile = ''
    outputfile = ''
    try:
        opts, args = getopt.getopt(argv,"hi:o:",["ifile=","ofile="])
    except getopt.GetoptError:
        print('test.py -i <inputfile> -o <outputfile>')
        sys.exit(2)
    for opt, arg in opts:
        if opt == '-h':
            print('test.py -i <inputfile> -o <outputfile>')
            sys.exit()
        elif opt in ("-i", "--ifile"):
            inputfile = arg
        elif opt in ("-o", "--ofile"):
            outputfile = arg
    print('输入的文件为：', inputfile)
    print('输出的文件为：', outputfile)
    PicFunc(inputfile,outputfile)

if __name__ == "__main__":
   main(sys.argv[1:])