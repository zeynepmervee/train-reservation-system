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

export async function getTripById(tripId) {
  const response = await fetch(`/api/v1/trips/${tripId}`)

  if (!response.ok) {
    throw new Error('Sefer bilgisi alınamadı.')
  }

  return response.json()
}
