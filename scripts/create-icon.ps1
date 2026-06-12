# Creates app-icon.ico from app-icon.png for jpackage on Windows.
param(
    [string]$PngPath = "$PSScriptRoot\..\src\main\resources\com\akash\connectfour\icon\app-icon.png",
    [string]$IcoPath = "$PSScriptRoot\..\src\main\resources\com\akash\connectfour\icon\app-icon.ico"
)

$ErrorActionPreference = "Stop"

if (-not (Test-Path $PngPath)) {
    Write-Error "PNG icon not found: $PngPath"
}

Add-Type -AssemblyName System.Drawing

$source = [System.Drawing.Image]::FromFile((Resolve-Path $PngPath))
try {
    $size = 256
    $bitmap = New-Object System.Drawing.Bitmap $size, $size
    $graphics = [System.Drawing.Graphics]::FromImage($bitmap)
    try {
        $graphics.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
        $graphics.Clear([System.Drawing.Color]::Transparent)
        $graphics.DrawImage($source, 0, 0, $size, $size)
        $handle = $bitmap.GetHicon()
        $icon = [System.Drawing.Icon]::FromHandle($handle)
        $stream = New-Object System.IO.FileStream($IcoPath, [System.IO.FileMode]::Create)
        try {
            $icon.Save($stream)
        }
        finally {
            $stream.Close()
            $icon.Dispose()
        }
    }
    finally {
        $graphics.Dispose()
        $bitmap.Dispose()
    }
}
finally {
    $source.Dispose()
}

Write-Host "Created icon: $IcoPath"
