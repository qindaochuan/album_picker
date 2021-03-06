import 'dart:async';

import 'package:flutter/services.dart';

class AlbumPicker {
  static const MethodChannel _channel =
      const MethodChannel('plugins.flutter.io/album_picker');

  static Future<List<String>> pickFile() async{
    final List<dynamic> temp = await _channel.invokeMethod("pickFile");
    List<String> paths = new List<String>();
    temp.forEach((t){
      paths.add(t.toString());
    });
    return paths;
  }

  static Future<String> videoCompress(String srcPath) async {
    String path =
    await _channel.invokeMethod<String>("videoCompress", <String, dynamic>{
      'srcPath': srcPath,
    });
    return path;
  }
}
