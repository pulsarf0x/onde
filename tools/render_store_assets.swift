import AppKit
import ImageIO
import UniformTypeIdentifiers

// Deterministic exports of Onde's existing waveform mark. No invented device UI.
let output = CommandLine.arguments.count > 1 ? CommandLine.arguments[1] : "play/graphics"
try FileManager.default.createDirectory(atPath: output, withIntermediateDirectories: true)

func canvas(_ width: Int, _ height: Int, alpha: Bool, draw: (CGContext) -> Void) -> CGImage {
    let bitmapInfo = alpha ? CGImageAlphaInfo.premultipliedLast.rawValue : CGImageAlphaInfo.noneSkipLast.rawValue
    let cg = CGContext(data: nil, width: width, height: height, bitsPerComponent: 8,
        bytesPerRow: width * 4, space: CGColorSpaceCreateDeviceRGB(), bitmapInfo: bitmapInfo)!
    NSGraphicsContext.saveGraphicsState()
    NSGraphicsContext.current = NSGraphicsContext(cgContext: cg, flipped: false)
    cg.setFillColor(NSColor.black.cgColor)
    cg.fill(CGRect(x: 0, y: 0, width: width, height: height))
    draw(cg)
    NSGraphicsContext.restoreGraphicsState()
    return cg.makeImage()!
}
func save(_ image: CGImage, _ name: String) throws {
    let url = URL(fileURLWithPath: output).appendingPathComponent(name)
    let destination = CGImageDestinationCreateWithURL(url as CFURL, UTType.png.identifier as CFString, 1, nil)!
    CGImageDestinationAddImage(destination, image, nil)
    guard CGImageDestinationFinalize(destination) else { fatalError("PNG export failed") }
}
func waveform(_ cg: CGContext, x: CGFloat, y: CGFloat, scale: CGFloat) {
    cg.saveGState(); cg.translateBy(x: x, y: y); cg.scaleBy(x: scale, y: -scale)
    cg.setStrokeColor(NSColor.white.cgColor); cg.setLineWidth(5); cg.setLineCap(.round)
    cg.move(to: CGPoint(x: 29,y:54))
    cg.addQuadCurve(to: CGPoint(x:41,y:54), control: CGPoint(x:35,y:31))
    cg.addQuadCurve(to: CGPoint(x:53,y:54), control: CGPoint(x:47,y:77))
    cg.addQuadCurve(to: CGPoint(x:65,y:54), control: CGPoint(x:59,y:31))
    cg.addQuadCurve(to: CGPoint(x:77,y:54), control: CGPoint(x:71,y:77))
    cg.strokePath()
    cg.setFillColor(NSColor.white.cgColor)
    cg.move(to: CGPoint(x:39,y:74)); cg.addLine(to: CGPoint(x:54,y:63)); cg.addLine(to: CGPoint(x:69,y:74)); cg.closePath(); cg.fillPath()
    cg.restoreGState()
}
func text(_ string: String, x: CGFloat, y: CGFloat, size: CGFloat, weight: NSFont.Weight, color: NSColor = .white) {
    (string as NSString).draw(at: NSPoint(x:x,y:y),withAttributes:[.font:NSFont.systemFont(ofSize:size,weight:weight),.foregroundColor:color])
}
let icon = canvas(512,512,alpha:true) { cg in waveform(cg,x:0,y:512,scale:512/108) }
try save(icon,"icon-512.png")
let feature = canvas(1024,500,alpha:false) { cg in
    cg.setFillColor(NSColor(calibratedRed:0.04,green:0.07,blue:0.10,alpha:1).cgColor)
    cg.fill(CGRect(x:0,y:0,width:1024,height:500))
    text("Onde",x:72,y:378,size:34,weight:.semibold)
    text("Votre musique.",x:72,y:260,size:64,weight:.semibold)
    text("Vos enceintes.",x:72,y:185,size:64,weight:.semibold)
    text("Audio Android vers AirPlay · Wi-Fi local",x:76,y:102,size:22,weight:.regular,
         color:NSColor(calibratedWhite:0.72,alpha:1))
    cg.setStrokeColor(NSColor(calibratedWhite:1,alpha:0.10).cgColor)
    cg.setLineWidth(1.5)
    for r: CGFloat in [64,100,136] { cg.strokeEllipse(in:CGRect(x:804-r,y:250-r,width:r*2,height:r*2)) }
    waveform(cg,x:660,y:394,scale:2.65)
}
try save(feature,"feature-1024x500.png")
print("Exported 512x512 RGBA icon and 1024x500 RGB feature graphic.")
