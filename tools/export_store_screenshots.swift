import AppKit
import ImageIO
import UniformTypeIdentifiers

// Lossless pixel export only: no crops, annotations, fabricated data or UI edits.
let input = CommandLine.arguments.count > 1 ? CommandLine.arguments[1] : "work/emulator-smoke"
let output = CommandLine.arguments.count > 2 ? CommandLine.arguments[2] : "play/graphics/screenshots"
try FileManager.default.createDirectory(atPath: output, withIntermediateDirectories: true)
for name in ["01-accueil", "02-reglages", "03-aide", "04-confidentialite", "05-accueil-sombre"] {
    let sourceURL = URL(fileURLWithPath: input).appendingPathComponent(name+".png")
    let source = CGImageSourceCreateWithURL(sourceURL as CFURL, nil)!
    let image = CGImageSourceCreateImageAtIndex(source, 0, nil)!
    let width=image.width, height=image.height
    let context = CGContext(data:nil,width:width,height:height,bitsPerComponent:8,bytesPerRow:width*4,
        space:CGColorSpaceCreateDeviceRGB(),bitmapInfo:CGImageAlphaInfo.noneSkipLast.rawValue)!
    context.setFillColor(NSColor.white.cgColor)
    context.fill(CGRect(x:0,y:0,width:width,height:height))
    context.draw(image,in:CGRect(x:0,y:0,width:width,height:height))
    let outputURL=URL(fileURLWithPath:output).appendingPathComponent(name+".png")
    let destination=CGImageDestinationCreateWithURL(outputURL as CFURL,UTType.png.identifier as CFString,1,nil)!
    CGImageDestinationAddImage(destination,context.makeImage()!,nil)
    guard CGImageDestinationFinalize(destination) else { fatalError("Screenshot export failed") }
    print("Exported \(name): \(width)×\(height) RGB")
}
