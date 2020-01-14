import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:album_picker/album_picker.dart';

void main() {
  const MethodChannel channel = MethodChannel('album_picker');

  TestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
    expect(await AlbumPicker.platformVersion, '42');
  });
}
