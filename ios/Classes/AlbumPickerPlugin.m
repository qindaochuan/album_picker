#import "AlbumPickerPlugin.h"
#import "TZImagePickerController.h"
#import "FLAnimatedImage.h"

@interface AlbumPickerPlugin()<TZImagePickerControllerDelegate>

@property(copy, nonatomic) FlutterResult result;

@end

@implementation AlbumPickerPlugin
{
    NSDictionary *_arguments;
    UIViewController *_viewController;
}

+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  FlutterMethodChannel* channel = [FlutterMethodChannel
      methodChannelWithName:@"plugins.flutter.io/album_picker"
            binaryMessenger:[registrar messenger]];
    UIViewController* viewController = [UIApplication sharedApplication].delegate.window.rootViewController;
  AlbumPickerPlugin* instance = [[AlbumPickerPlugin alloc] initWithViewControllver:viewController];
  [registrar addMethodCallDelegate:instance channel:channel];
}

- (instancetype)initWithViewControllver:(UIViewController *)viewController {
    if(self){
        _viewController = viewController;
    }
    return self;
}

- (void)handleMethodCall:(FlutterMethodCall*)call result:(FlutterResult)result {
    if (self.result) {
        self.result([FlutterError errorWithCode:@"multiple_request"
                                        message:@"Cancelled by a second request"
                                        details:nil]);
        self.result = nil;
    }
    
  if ([@"pickFile" isEqualToString:call.method]) {
      self.result = result;
      _arguments = call.arguments;
      [self pickFile];
  } else {
    result(FlutterMethodNotImplemented);
  }
}

- (void)pickFile{
    TZImagePickerController *imagePickerVc = [[TZImagePickerController alloc] initWithMaxImagesCount:5 columnNumber:4 delegate:self pushPhotoPickerVc:YES result:self.result];
       // imagePickerVc.barItemTextColor = [UIColor blackColor];
       // [imagePickerVc.navigationBar setTitleTextAttributes:@{NSForegroundColorAttributeName : [UIColor blackColor]}];
       // imagePickerVc.navigationBar.tintColor = [UIColor blackColor];
       // imagePickerVc.naviBgColor = [UIColor whiteColor];
       // imagePickerVc.navigationBar.translucent = NO;
       
   #pragma mark - 五类个性化设置，这些参数都可以不传，此时会走默认设置
       imagePickerVc.isSelectOriginalPhoto = true;
       
       imagePickerVc.allowTakePicture = false; // 在内部显示拍照按钮
       imagePickerVc.allowTakeVideo = false;   // 在内部显示拍视频按
       imagePickerVc.videoMaximumDuration = 10; // 视频最大拍摄时间
       [imagePickerVc setUiImagePickerControllerSettingBlock:^(UIImagePickerController *imagePickerController) {
           imagePickerController.videoQuality = UIImagePickerControllerQualityTypeHigh;
       }];
       
       // imagePickerVc.photoWidth = 1600;
       // imagePickerVc.photoPreviewMaxWidth = 1600;
       
       // 2. Set the appearance
       // 2. 在这里设置imagePickerVc的外观
       // imagePickerVc.navigationBar.barTintColor = [UIColor greenColor];
       // imagePickerVc.oKButtonTitleColorDisabled = [UIColor lightGrayColor];
       // imagePickerVc.oKButtonTitleColorNormal = [UIColor greenColor];
       // imagePickerVc.navigationBar.translucent = NO;
       imagePickerVc.iconThemeColor = [UIColor colorWithRed:31 / 255.0 green:185 / 255.0 blue:34 / 255.0 alpha:1.0];
       imagePickerVc.showPhotoCannotSelectLayer = YES;
       imagePickerVc.cannotSelectLayerColor = [[UIColor whiteColor] colorWithAlphaComponent:0.8];
       [imagePickerVc setPhotoPickerPageUIConfigBlock:^(UICollectionView *collectionView, UIView *bottomToolBar, UIButton *previewButton, UIButton *originalPhotoButton, UILabel *originalPhotoLabel, UIButton *doneButton, UIImageView *numberImageView, UILabel *numberLabel, UIView *divideLine) {
           [doneButton setTitleColor:[UIColor redColor] forState:UIControlStateNormal];
       }];
       /*
       [imagePickerVc setAssetCellDidSetModelBlock:^(TZAssetCell *cell, UIImageView *imageView, UIImageView *selectImageView, UILabel *indexLabel, UIView *bottomView, UILabel *timeLength, UIImageView *videoImgView) {
           cell.contentView.clipsToBounds = YES;
           cell.contentView.layer.cornerRadius = cell.contentView.tz_width * 0.5;
       }];
        */
       
       // 3. Set allow picking video & photo & originalPhoto or not
       // 3. 设置是否可以选择视频/图片/原图
       imagePickerVc.allowPickingVideo = true;
       imagePickerVc.allowPickingImage = true;
       imagePickerVc.allowPickingOriginalPhoto = false;
       imagePickerVc.allowPickingGif = true;
       imagePickerVc.allowPickingMultipleVideo = true; // 是否可以多选视频
       
       // 4. 照片排列按修改时间升序
       imagePickerVc.sortAscendingByModificationDate = true;
       
       // imagePickerVc.minImagesCount = 3;
       // imagePickerVc.alwaysEnableDoneBtn = YES;
       
       // imagePickerVc.minPhotoWidthSelectable = 3000;
       // imagePickerVc.minPhotoHeightSelectable = 2000;
       
       /// 5. Single selection mode, valid when maxImagesCount = 1
       /// 5. 单选模式,maxImagesCount为1时才生效
       imagePickerVc.showSelectBtn = NO;
       imagePickerVc.allowCrop = false;
       imagePickerVc.needCircleCrop = false;
       // 设置竖屏下的裁剪尺寸
       NSInteger left = 30;
       NSInteger widthHeight = _viewController.view.frame.size.width - 2 * left;
       NSInteger top = (_viewController.view.frame.size.width - widthHeight) / 2;
       imagePickerVc.cropRect = CGRectMake(left, top, widthHeight, widthHeight);
       imagePickerVc.scaleAspectFillCrop = YES;
       // 设置横屏下的裁剪尺寸
       // imagePickerVc.cropRectLandscape = CGRectMake((self.view.tz_height - widthHeight) / 2, left, widthHeight, widthHeight);
       /*
        [imagePickerVc setCropViewSettingBlock:^(UIView *cropView) {
        cropView.layer.borderColor = [UIColor redColor].CGColor;
        cropView.layer.borderWidth = 2.0;
        }];*/
       
       //imagePickerVc.allowPreview = NO;
       // 自定义导航栏上的返回按钮
       /*
       [imagePickerVc setNavLeftBarButtonSettingBlock:^(UIButton *leftButton){
           [leftButton setImage:[UIImage imageNamed:@"back"] forState:UIControlStateNormal];
           [leftButton setImageEdgeInsets:UIEdgeInsetsMake(0, -10, 0, 20)];
       }];
       imagePickerVc.delegate = self;
       */
       
       // Deprecated, Use statusBarStyle
       // imagePickerVc.isStatusBarDefault = NO;
       imagePickerVc.statusBarStyle = UIStatusBarStyleLightContent;
       
       // 设置是否显示图片序号
       imagePickerVc.showSelectedIndex = false;
       
       // 自定义gif播放方案
       [[TZImagePickerConfig sharedInstance] setGifImagePlayBlock:^(TZPhotoPreviewView *view, UIImageView *imageView, NSData *gifData, NSDictionary *info) {
           FLAnimatedImage *animatedImage = [FLAnimatedImage animatedImageWithGIFData:gifData];
           FLAnimatedImageView *animatedImageView;
           for (UIView *subview in imageView.subviews) {
               if ([subview isKindOfClass:[FLAnimatedImageView class]]) {
                   animatedImageView = (FLAnimatedImageView *)subview;
                   animatedImageView.frame = imageView.bounds;
                   animatedImageView.animatedImage = nil;
               }
           }
           if (!animatedImageView) {
               animatedImageView = [[FLAnimatedImageView alloc] initWithFrame:imageView.bounds];
               animatedImageView.runLoopMode = NSDefaultRunLoopMode;
               [imageView addSubview:animatedImageView];
           }
           animatedImageView.animatedImage = animatedImage;
       }];
       
       // 设置首选语言 / Set preferred language
       // imagePickerVc.preferredLanguage = @"zh-Hans";
       
       // 设置languageBundle以使用其它语言 / Set languageBundle to use other language
       // imagePickerVc.languageBundle = [NSBundle bundleWithPath:[[NSBundle mainBundle] pathForResource:@"tz-ru" ofType:@"lproj"]];
       
   #pragma mark - 到这里为止
       
       // You can get the photos by block, the same as by delegate.
       // 你可以通过block或者代理，来得到用户选择的照片.
       [imagePickerVc setDidFinishPickingPhotosHandle:^(NSArray<UIImage *> *photos, NSArray *assets, BOOL isSelectOriginalPhoto) {

       }];
       
       imagePickerVc.modalPresentationStyle = UIModalPresentationFullScreen;
       [_viewController presentViewController:imagePickerVc animated:YES completion:nil];
}

@end
