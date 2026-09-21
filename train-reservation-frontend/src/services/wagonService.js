export async function getWagonsByTrainId(trainId) {
  const response = await fetch(`/api/v1/trains/${trainId}/wagons`)

  if (!response.ok) {
    throw new Error('Vagon ve koltuk bilgileri alınamadı.')
  }

  return response.json()
}
