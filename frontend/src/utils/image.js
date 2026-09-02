const FALLBACK_IMAGES = {
  vaccine: '/default-vaccine.svg',
  community: '/default-community.svg',
  news: '/default-news.svg',
  banner: '/default-banner.svg'
}

/**
 * 统一处理接口返回的图片地址，避免空字段造成破图或空白区域。
 */
export function resolveImageSrc(value, type = 'news') {
  if (typeof value === 'string' && value.trim()) {
    return value.trim()
  }
  return FALLBACK_IMAGES[type] || FALLBACK_IMAGES.news
}

export { FALLBACK_IMAGES }
