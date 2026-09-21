export async function searchTrips(departureStationId, arrivalStationId, date) {
  const parameters = new URLSearchParams({
    departureStationId,
    arrivalStationId,
    date,
  })

  const response = await fetch(`/api/v1/trips/search?${parameters}`)

  if (!response.ok) {
    throw new Error('Seferler alınamadı.')
  }

  return response.json()
}
