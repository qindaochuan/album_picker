#import "AlbumPickerPlugin.h"

@interface AlbumPickerPlugin()

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
    NSLog(@"iOS called pickFile");
    self.result(@"");
}

@end
