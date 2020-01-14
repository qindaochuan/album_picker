import 'dart:async';

import 'package:flutter/services.dart';

class AlbumPicker {
  static const MethodChannel _channel =
      const MethodChannel('album_picker');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }
}
