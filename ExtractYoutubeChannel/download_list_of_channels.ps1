# download-channels.ps1
# Download videos for a list of channels using yt-dlp

param(
  [string[]] $Channels,
  [string]   $ChannelFile,
  [string]   $OutDir     = "D:\_Videos",
  [string]   $Archive    = "$env:USERPROFILE\Videos\downloaded.txt",
  [int]      $MaxRetries = 2,
  [int]      $BackoffSec = 5,
  [switch]   $DryRun
)

[Console]::OutputEncoding = [System.Text.Encoding]::UTF8
$OutputEncoding           = [System.Text.Encoding]::UTF8

# --- Locate yt-dlp ---
$ytCmd = Get-Command yt-dlp -ErrorAction SilentlyContinue
if ($ytCmd) {
  $yt = $ytCmd.Source
} else {
  $yt = Join-Path $PSScriptRoot "yt-dlp.exe"
}

if (-not (Test-Path $yt)) {
  Write-Host "[ERROR] yt-dlp not found in PATH or $PSScriptRoot" -ForegroundColor Red
  Write-Host "        Please install yt-dlp or put yt-dlp.exe in the script folder." -ForegroundColor Yellow
  return
}

# --- Prepare folders ---
$null = New-Item -ItemType Directory -Force -Path $OutDir
if (-not (Test-Path $Archive)) { 
  New-Item -ItemType File -Force -Path $Archive | Out-Null 
}

# --- Collect inputs ---
$fromFile = @()
if ($ChannelFile) {
  $fromFile = Get-Content $ChannelFile -Encoding UTF8 | Where-Object { $_ -and ($_ -notmatch '^\s*#') }
}

$allInputs = @() + $Channels + $fromFile
if (-not $allInputs) {
  Write-Host "[ERROR] No channels provided (use -Channels or -ChannelFile)." -ForegroundColor Red
  return
}

# --- Normalize to YouTube URLs ---
function To-ChannelUrl([string]$s) {
  $s = $s.Trim()
  if ($s -match '^https?://') { return $s }
  if ($s -match '^@') { return "https://www.youtube.com/$s/videos" }
  return "https://www.youtube.com/@$s/videos"
}
$urls = $allInputs | ForEach-Object { To-ChannelUrl $_ } | Select-Object -Unique

# --- Debug info ---
Write-Host "`n[INFO] yt-dlp path: $yt" -ForegroundColor Cyan
Write-Host "[INFO] Output folder: $OutDir" -ForegroundColor Cyan
Write-Host "[INFO] Archive file: $Archive" -ForegroundColor Cyan
Write-Host "[INFO] Channels to process:" -ForegroundColor Cyan
$urls | ForEach-Object { Write-Host "  $_" }

# --- yt-dlp options ---
$outTpl = Join-Path $OutDir '%(channel)s\%(upload_date)s - %(title)s [%(id)s].%(ext)s'
$common = @(
  '-ciw',
  '-o', $outTpl,
  '--download-archive', $Archive,
  '-f', 'bv*[height<=1080]+ba/b[height<=1080]',
  '--merge-output-format', 'mp4'
)

# --- Download loop ---
foreach ($url in $urls) {
  Write-Host "`n=== Channel: $url ===" -ForegroundColor Cyan

  if ($DryRun) { 
    Write-Host "$yt $($common -join ' ') $url"
    continue 
  }

  $attempt = 0
  do {
    $attempt++
    & $yt @common $url 2>&1 | Tee-Object -Variable ytOutput
    $code = $LASTEXITCODE

    if ($code -eq 0) {
      Write-Host "OK ($url)" -ForegroundColor Green
      break
    }
    elseif ($attempt -lt $MaxRetries) {
      $delay = $BackoffSec * $attempt
      Write-Warning "Retrying in $delay sec... Exit code: $code"
      Start-Sleep -Seconds $delay
    } else {
      Write-Error "Failed after $MaxRetries attempt(s) for $url (exit $code)."
      if ($ytOutput) { 
        Write-Host "[yt-dlp output]:`n$ytOutput" -ForegroundColor DarkYellow 
      }
    }
  } while ($attempt -lt $MaxRetries)
}
