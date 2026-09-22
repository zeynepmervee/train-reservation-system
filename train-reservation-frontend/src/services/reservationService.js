export async function createReservation(request) {
  const response = await fetch('/api/v1/reservations', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(request),
  })

  if (response.status === 409) {
    throw new Error(
      'Seçtiğiniz koltuklardan biri başka bir yolcu tarafından alınmış.',
    )
  }

  if (response.status === 400) {
    throw new Error('Yolcu bilgilerini kontrol ediniz.')
  }

  if (!response.ok) {
    throw new Error('Rezervasyon oluşturulamadı.')
  }

  return response.json()
}
