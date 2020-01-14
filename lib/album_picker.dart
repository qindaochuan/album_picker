import 'dart:async';

import 'package:flutter/services.dart';

class AlbumPicker {
  static const MethodChannel _channel =
      const MethodChannel('plugins.flutter.io/album_picker');

  static Future<String> pickFile() async{
    final String path = await _channel.invokeMethod("pickFile");
    return path;
  }
}
