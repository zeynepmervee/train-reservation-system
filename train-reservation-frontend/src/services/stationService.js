export async function getStations() {
  const response = await fetch('/api/v1/stations')

  if (!response.ok) {
    throw new Error('İstasyonlar alınamadı.')
  }

  return response.json()
}
