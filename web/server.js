const http = require('http');
const fs = require('fs');
const path = require('path');

const PORT = process.env.PORT || 8080;

const catalog = {
  'vid_6t9_01': {
    id: 'vid_6t9_01',
    title: 'Neon Cyber Drift • 6T9 Special',
    description: 'High octane night chase through glowing futuristic streets. Full high bitrate video.',
    videoUrl: 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4',
    thumbnailUrl: 'https://images.unsplash.com/photo-1542751371-adc38448a05e?w=800&auto=format&fit=crop',
    duration: '0:15',
    isLocked: true,
    isDownloadEnabled: true,
    viewsCount: 1240,
    downloadsCount: 380,
    category: 'Action'
  },
  'vid_6t9_02': {
    id: 'vid_6t9_02',
    title: 'Hyper Speed Escape • Extreme Stunts',
    description: 'Adrenaline packed parkour and hyper speed action. Pure cinematic visual soundscape.',
    videoUrl: 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4',
    thumbnailUrl: 'https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=800&auto=format&fit=crop',
    duration: '0:15',
    isLocked: false,
    isDownloadEnabled: true,
    viewsCount: 890,
    downloadsCount: 145,
    category: 'Trending'
  },
  'vid_6t9_03': {
    id: 'vid_6t9_03',
    title: 'Urban Sunset Meltdown • Ultra 4K',
    description: 'Golden hour vibes across metropolitan skyscrapers with vibrant synth beats.',
    videoUrl: 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerJoyBlazes.mp4',
    thumbnailUrl: 'https://images.unsplash.com/photo-1508739773434-c26b3d09e071?w=800&auto=format&fit=crop',
    duration: '0:15',
    isLocked: true,
    isDownloadEnabled: true,
    viewsCount: 2150,
    downloadsCount: 610,
    category: 'Trending'
  },
  'vid_6t9_04': {
    id: 'vid_6t9_04',
    title: 'Midnight Quantum Core Meltdown',
    description: 'Sci-fi VFX showcase of power plant explosion and futuristic mecha warfare.',
    videoUrl: 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerMeltdowns.mp4',
    thumbnailUrl: 'https://images.unsplash.com/photo-1511512578047-dfb367046420?w=800&auto=format&fit=crop',
    duration: '0:15',
    isLocked: true,
    isDownloadEnabled: false,
    viewsCount: 540,
    downloadsCount: 0,
    category: 'Shorts'
  },
  'vid_6t9_05': {
    id: 'vid_6t9_05',
    title: 'Tokyo Underground Beats & Street Life',
    description: 'Night market neon reflections, rain, and lo-fi hip hop bass.',
    videoUrl: 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/WeAreGoingOnBullrun.mp4',
    thumbnailUrl: 'https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=800&auto=format&fit=crop',
    duration: '0:30',
    isLocked: false,
    isDownloadEnabled: true,
    viewsCount: 3420,
    downloadsCount: 920,
    category: 'Shorts'
  }
};

const server = http.createServer((req, res) => {
  // Enable CORS
  res.setHeader('Access-Control-Allow-Origin', '*');
  res.setHeader('Access-Control-Allow-Methods', 'GET, POST, OPTIONS');
  res.setHeader('Access-Control-Allow-Headers', 'Content-Type');

  if (req.method === 'OPTIONS') {
    res.writeHead(204);
    res.end();
    return;
  }

  const url = new URL(req.url, `http://${req.headers.host}`);

  // API Endpoints
  if (url.pathname === '/api/videos') {
    res.writeHead(200, { 'Content-Type': 'application/json' });
    res.end(JSON.stringify(Object.values(catalog)));
    return;
  }

  if (url.pathname.startsWith('/api/videos/')) {
    const id = url.pathname.replace('/api/videos/', '');
    const item = catalog[id];
    if (item) {
      res.writeHead(200, { 'Content-Type': 'application/json' });
      res.end(JSON.stringify(item));
    } else {
      res.writeHead(404, { 'Content-Type': 'application/json' });
      res.end(JSON.stringify({ error: 'Video not found' }));
    }
    return;
  }

  if (url.pathname === '/api/settings/admob') {
    res.writeHead(200, { 'Content-Type': 'application/json' });
    res.end(JSON.stringify({
      appId: "ca-app-pub-3940256099942544~3347511713",
      rewardedAdUnitId: "ca-app-pub-3940256099942544/5224354917",
      useTestAds: true
    }));
    return;
  }

  // Web Video Sharing Page route
  if (url.pathname.startsWith('/video/') || url.pathname === '/' || url.pathname === '/index.html') {
    const indexPath = path.join(__dirname, 'index.html');
    fs.readFile(indexPath, 'utf8', (err, content) => {
      if (err) {
        res.writeHead(500);
        res.end('Error loading video page');
      } else {
        res.writeHead(200, { 'Content-Type': 'text/html; charset=utf-8' });
        res.end(content);
      }
    });
    return;
  }

  res.writeHead(404);
  res.end('Not Found');
});

server.listen(PORT, () => {
  console.log(`SHORT 6T9 Backend & Video Web Page listening on port ${PORT}`);
});
